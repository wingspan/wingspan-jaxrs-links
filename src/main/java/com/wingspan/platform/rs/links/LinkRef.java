package com.wingspan.platform.rs.links;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * A reference to a link on a resource class.
 *
 * @see com.wingspan.platform.rs.links.LinkTarget
 */
public class LinkRef
{
    String   name;
    Class<?> resource;
    String   locatorMethod;

    Method  resourceMethod;

    LinkProcessor linkProcessor;

    /**
     * Creates a standard link reference for a named-link on a given resource class.
     *
     * @param name The name of the link in a LinkTarget annotation
     * @param resource The resource class hosting the LinkTargets
     */
    public LinkRef(String name, Class<?> resource)
    {
        this(name, resource, null, null, null);
    }

    /**
     * Creates a standard link reference for a named-link on a given resource class.
     *
     * @param name The name of the link in a LinkTarget annotation
     * @param resource The resource class hosting the LinkTargets
     */
    public LinkRef(String name, Class<?> resource, LinkProcessor linkProcessor)
    {
        this(name, resource, null, null, linkProcessor);
    }

    private LinkRef(String name, Class<?> resource, String locatorMethod, Method resourceMethod, LinkProcessor linkProcessor)
    {
        this.name = name;
        this.resource = resource;
        this.locatorMethod = locatorMethod;
        this.resourceMethod = resourceMethod;
        this.linkProcessor = linkProcessor;

        if (this.resourceMethod == null) {
            this.resourceMethod = LinkBuilder.getMethodForLink(this);
        }
    }

    /**
     * Create a link on a sub-resource that is created by a particular locator method on a parent class.
     *
     * @param resourceClass The parent resource class
     * @param methodName The name of the sub-resource locator method on the parent class
     * @return A LinkRef for the sub-resource
     */
    public LinkRef fromParentMethod(Class<?> resourceClass, String methodName)
    {
        return new LinkRef(name, resourceClass, methodName, null, null);
    }

    /**
     * Create a LinkRef to the same LinkTarget but with an alternate name.
     *
     * @param name The alternate name for the link
     * @return A LinkRef with the new name
     */
    public LinkRef overrideName(String name)
    {
        return new LinkRef(name, resource, locatorMethod, resourceMethod, linkProcessor);
    }

    /**
     * Create a LinkRef to the same LinkTarget but with different link processors.
     *
     * @param linkProcessor The alternate list of link processors
     * @return A LinkRef with the new name
     */
    public LinkRef overrideLinkProcessor(LinkProcessor<?> linkProcessor)
    {
        return new LinkRef(name, resource, locatorMethod, resourceMethod, linkProcessor);
    }

    public String getName()
    {
        return name;
    }

    public Class<?> getResource()
    {
        return resource;
    }

    public String getLocatorMethod()
    {
        return locatorMethod;
    }

    Method getResourceMethod()
    {
        return resourceMethod;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LinkRef linkRef = (LinkRef) o;

        return Objects.equals(name, linkRef.name) &&
                Objects.equals(resource, linkRef.resource) &&
                Objects.equals(locatorMethod, linkRef.locatorMethod);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name, resource, locatorMethod);
    }
}
