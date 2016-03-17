package com.wingspan.platform.rs.links;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;

/**
 * The primary API for generating links from annotated resources.
 */
public class LinkBuilder
{
    private UriBuilder _builder;

    /**
     * Create a new LinkBuilder with the given UriBuilder.
     * @param baseBuilder The URI builder that will be used as the starting point
     * @return A LinkBuilder ready to go
     */
    public static LinkBuilder create(UriBuilder baseBuilder)
    {
        return new LinkBuilder(baseBuilder);
    }

    public LinkBuilder(UriBuilder builder)
    {
        _builder = builder;
    }

    /**
     * Build a specific named URI, defined by the name and the resource class inside the LinkRef.
     *
     * @param theLink Link reference
     * @return The built URI
     */
    public URI buildUri(LinkRef theLink)
    {
        return newUriBuilder(theLink).build();
    }

    /**
     * Build a specific named URI, including arguments for a default query string parameter.
     * The query parameter name is set on the LinkTarget annotation for the given link reference.
     *
     * @param link Link reference
     * @param params Parameter values to be used
     * @return The built URI
     * @see com.wingspan.platform.rs.links.LinkTarget
     */
    public URI buildUriWithQuery(LinkRef link, Object... params)
    {
        UriBuilder builder = newUriBuilder(link);

        if (params != null) {
            LinkTarget target = link.getResourceMethod().getAnnotation(LinkTarget.class);
            builder.queryParam(target.defaultQuery(), params);
        }

        return builder.build();
    }

    /**
     * Build a specific named URI for a given resource class.
     *
     * @param theLink Link reference
     * @param paramBean Bean containing properties for URI template
     * @return The built URI
     */
    @SuppressWarnings("unchecked")
    public URI buildUri(LinkRef theLink, Object paramBean)
    {
        try {
            UriBuilder builder = newUriBuilder(theLink);
            LinkTarget target = theLink.getResourceMethod().getAnnotation(LinkTarget.class);

            if (target.condition() != Predicate.class) {
                // Run the bean through the predicate to see if the link should be generated.
                if (!predicateFor(target).test(paramBean)) {
                    return null;
                }
            }

            String[] paramNames = target.templateParams();
            Object[] templateValues = new Object[paramNames.length];

            for (int i = 0; i < templateValues.length; ++i) {
                templateValues[i] = Beans.readValue(paramBean, paramNames[i]);

                // If any of the necessary values are missing, the URL cannot be generated. Return null in this case,
                // because it allows for a more convenient usage model that prevents URLs from being generated when their values are dynamic.
                if (templateValues[i] == null) {
                    return null;
                }

                // A bug in Jersey 1.x UriBuilder leaves '/' in template values for URI path segments.
                // https://java.net/jira/browse/JAX_RS_SPEC-70
                templateValues[i] = removeSlashes(templateValues[i]);
            }

            if (!target.defaultQuery().isEmpty()) {
                // Don't need to check for null bean property values here, because query parameters are always optional
                builder.queryParam(target.defaultQuery(), Beans.readValue(paramBean, target.defaultQuery()));
            }

            // special handling for the link processors
            for (LinkProcessor processor : linkProcessorsFor(target)) {
                builder = processor.processLink(builder, templateValues, paramBean);
            }

            // then run any link processors link processors from the link ref
            if (theLink.linkProcessor != null) {
                builder = theLink.linkProcessor.processLink(builder, templateValues, paramBean);
            }

            return builder.build(templateValues);
        }
        catch (Throwable t) {
            throw new LinkBuilderException(t, theLink, paramBean);
        }
    }

    /**
     * Build a specific named URI but return the builder for additional processing.
     *
     * @param link Name of an annotation LinkSource
     * @return A new builder ready to build
     */
    public UriBuilder newUriBuilder(LinkRef link)
    {
        Method linkMethod = link.getResourceMethod();
        UriBuilder builder = _builder.clone().path(link.getResource());

        // Note: Sub-resources don't have a Path annotation on the class
        if (link.getLocatorMethod() != null) {
            builder = builder.path(link.getResource(), link.getLocatorMethod());
        }

        // Methods don't always have a Path annotation since they may represent the primary resource
        // for the class.
        if (linkMethod.isAnnotationPresent(Path.class)) {
            builder.path(linkMethod);
        }

        return builder;
    }

    static class Beans
    {
        public static Object readValue(Object bean, String property)
        {
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());

                for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
                    if (pd.getName().equals(property)) {
                        return pd.getReadMethod().invoke(bean);
                    }
                }
            }
            catch (IntrospectionException | ReflectiveOperationException e) {
                throw new IllegalArgumentException("Unknown property name or bad bean");
            }

            return null;
        }
    }

    //removes both forward and back slashes
    static final Pattern SLASH_REGEX = Pattern.compile("[/\\\\]");

    private Object removeSlashes(Object templateValue)
    {
        if (templateValue instanceof String) {
            // Remove the slashes because we can't put percent-encodings in here.
            if (((String) templateValue).indexOf('/') != -1) {
                return SLASH_REGEX.matcher((String) templateValue).replaceAll("");
            }
        }
        return templateValue;
    }

    static Method getMethodForLink(LinkRef link)
    {
        for (Method m : link.getResource().getMethods()) {
            // If it's a sub-resource link, so we need to look for the method on the return type of the
            // resource locator method. That's the class that the link references.
            if (link.locatorMethod != null) {
                if (!m.getName().equals(link.locatorMethod)) {
                    continue;
                }

                m = getMethodForLink(new LinkRef(link.name, m.getReturnType()));
                return m;
            }

            if (!m.isAnnotationPresent(LinkTarget.class)) {
                continue;
            }

            if (m.getAnnotation(LinkTarget.class).name().equals(link.name)) {
                return m;
            }
        }

        // I like the idea of not returning null, but that may not work in practice.
        throw new IllegalArgumentException("Unknown link name");
    }

    private static Iterable<LinkProcessor> linkProcessorsFor(LinkTarget target)
    {
        Class<? extends LinkProcessor> classes[] = target.linkProcessors();

        if (classes.length == 0) {
            return Collections.emptyList();
        }

        if (classes.length == 1) {
            return Collections.singleton(newLinkProcessor(classes[0], target));
        }

        List<LinkProcessor> processors = new ArrayList<>();

        for (Class<? extends LinkProcessor> clazz : classes) {
            processors.add(newLinkProcessor(clazz, target));
        }
        return processors;
    }

    private static LinkProcessor newLinkProcessor(Class<? extends LinkProcessor> clazz, LinkTarget target)
    {
        try {
            // First try a constructor that takes a LinkSource.
            // If the implementation doesn't require it, try a no-arg constructor.
            try {
                return clazz.getConstructor(LinkTarget.class).newInstance(target);
            }
            catch (NoSuchMethodException e) {
                return clazz.newInstance();
            }
        }
        catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("Could not instantiate ILinkProcessor implementation.", e);
        }
    }

    private static Predicate predicateFor(LinkTarget target)
    {
        try {
            return target.condition().newInstance();
        }
        catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("Could not instantiate Predicate implementation.", e);
        }
    }
}
