package com.wingspan.platform.rs.links;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import javax.ws.rs.Path;

/**
 * A container of registrations for accessing links. This registry can apply to one or more model types,
 * depending on how context resolution is implemented. Typically, each model type has its own registry instance.
 *
 * @see com.wingspan.platform.rs.links.LinkRegistryProvider
 */
public class LinkRegistry
{
    private Collection<LinkRef> links;

    /**
     * Create a new registry for links rooted at the given URI.
     *
     * @param links Link references in this registry
     */
    public LinkRegistry(LinkRef... links)
    {
        this(Arrays.asList(links));
    }

    /**
     * Create a new registry for links rooted at the given URI.
     *
     * @param links Link references in this registry
     */
    public LinkRegistry(Collection<LinkRef> links)
    {
        this.links = Collections.unmodifiableCollection(links);
    }

    /**
     * Returns the links in this registry.
     *
     * @return A collection of LinkRef objects.
     */
    public Collection<LinkRef> getLinks()
    {
        return links;
    }

    /**
     * Create a registry using the given resource class.
     * @param resourceClass Resource class
     * @return A link registry with the resource's links
     */
    public static LinkRegistry fromResource(Class<?> resourceClass)
    {
        return fromResource(resourceClass, null);
    }

    /**
     * Create a registry using the given resource class and an optional link group
     * @param resourceClass Resource class
     * @param linkGroup Link group class or interface
     * @return A link registry with the resource's links
     */
    public static LinkRegistry fromResource(Class<?> resourceClass, Class<?> linkGroup)
    {
        Objects.requireNonNull(resourceClass, "A resource class is required");

        if (!resourceClass.isAnnotationPresent(Path.class)) {
            throw new IllegalArgumentException("The resource class must have a Path annotation to build links.");
        }

        HashSet<LinkRef> newLinks = new HashSet<>();

        for (Method method : resourceClass.getMethods()) {
            LinkTarget linkTarget = method.getAnnotation(LinkTarget.class);

            if (linkTarget == null) {
                continue;
            }

            if (!isGroupInTarget(linkTarget, linkGroup)) {
                continue;
            }

            newLinks.add(new LinkRef(linkTarget.name(), resourceClass));
        }

        return new LinkRegistry(newLinks);
    }

    /* Internal methods */

    LinkRegistry combineWith(LinkRegistry registry)
    {
        HashSet<LinkRef> newLinks = new HashSet<>(this.links);
        newLinks.addAll(registry.links);

        return new LinkRegistry(newLinks);
    }

    static boolean isGroupInTarget(LinkTarget linkTarget, Class<?> testGroup)
    {
        if (testGroup == null) {
            return true;
        }

        for (Class<?> group : linkTarget.groups()) {
            if (group.isAssignableFrom(testGroup)) {
                return true;
            }
        }
        return false;
    }
}
