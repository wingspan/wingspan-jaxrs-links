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
import java.util.concurrent.ConcurrentHashMap;
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
            LinkTarget target = getMethodForLink(link).getAnnotation(LinkTarget.class);
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
        UriBuilder builder = newUriBuilder(theLink);

        LinkTarget target = getMethodForLink(theLink).getAnnotation(LinkTarget.class);
        String[] paramNames = target.templateParams();
        Object[] templateValues = new Object[paramNames.length];

        for (int i = 0; i < templateValues.length; ++i) {
            templateValues[i] = Beans.readValue(paramBean, paramNames[i]);

            // If any of the necessary values are missing, the URL cannot be generated. Return null in this case,
            // because it allows for a more convenient usage model that prevents URLs from being generated when their values are dynamic.
            if (templateValues[i] == null) {
                return null;
            }
        }

        if (!target.defaultQuery().isEmpty()) {
            // Don't need to check for null bean property values here, because query parameters are always optional
            builder.queryParam(target.defaultQuery(), Beans.readValue(paramBean, target.defaultQuery()));
        }

        // special handling for the link processors
        for (LinkProcessor processor : linkProcessorsFor(target)) {
            builder = processor.processLink(builder, templateValues, paramBean);
        }

        return builder.build(templateValues);
    }

    /**
     * Build a specific named URI but return the builder for additional processing.
     *
     * @param link Name of an annotation LinkSource
     * @return A new builder ready to build
     */
    public UriBuilder newUriBuilder(LinkRef link)
    {
        Method linkMethod = getMethodForLink(link);
        LinkTarget linkTarget = linkMethod.getAnnotation(LinkTarget.class);
        UriBuilder builder;

        // Note: Sub-resources don't have a Path annotation on the class
        if (linkTarget.parentLink().isEmpty()) {
            builder = _builder.clone().path(link.getResource());
        } else {
            builder = newUriBuilder(new LinkRef(linkTarget.parentLink(), linkTarget.parentResource()));
        }

        // To generate a link to this method, either the method has a Path annotation or it doesn't.
        if (linkMethod.isAnnotationPresent(Path.class)) {
            builder.path(link.getResource(), linkMethod.getName());
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

    private static Method getMethodForLink(LinkRef link)
    {
        if (s_methodCache.containsKey(link)) {
            return s_methodCache.get(link);
        }

        for (Method m : link.getResource().getMethods()) {
            LinkTarget linkTarget = m.getAnnotation(LinkTarget.class);

            if (linkTarget == null) {
                continue;
            }

            if (linkTarget.name().equals(link.getName())) {
                s_methodCache.put(link, m);
                return m;
            }
        }

        // I like the idea of not returning null, but that may not work in practice.
        throw new IllegalArgumentException("Unknown link name");
    }

    static final ConcurrentHashMap<LinkRef, Method> s_methodCache = new ConcurrentHashMap<>();

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
}