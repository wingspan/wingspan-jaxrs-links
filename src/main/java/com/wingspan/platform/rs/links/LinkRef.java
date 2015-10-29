package com.wingspan.platform.rs.links;

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

    public static final LinkRef NONE = new LinkRef("", Void.class);

    public LinkRef(String name, Class<?> resource)
    {
        this.name = name;
        this.resource = resource;
    }

    private LinkRef(String name, Class<?> resource, String locatorMethod)
    {
        this.name = name;
        this.resource = resource;
        this.locatorMethod = locatorMethod;
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

    public LinkRef fromParentMethod(Class<?> resourceClass, String methodName)
    {
        return new LinkRef(name, resourceClass, methodName);
    }
}
