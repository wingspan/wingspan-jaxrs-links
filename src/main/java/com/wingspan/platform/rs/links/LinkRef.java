package com.wingspan.platform.rs.links;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
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

    List<Class<? extends LinkProcessor>> additionalLinkProcessors;

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
    public LinkRef(String name, Class<?> resource, List<Class<? extends LinkProcessor>> additionalLinkProcessors)
    {
        this(name, resource, null, null, additionalLinkProcessors);

    }

    private LinkRef(String name, Class<?> resource, String locatorMethod, Method resourceMethod, List<Class<? extends LinkProcessor>> additionalLinkProcessors)
    {
        this.name = name;
        this.resource = resource;
        this.locatorMethod = locatorMethod;
        this.resourceMethod = resourceMethod;

        if (this.resourceMethod == null) {
            this.resourceMethod = LinkBuilder.getMethodForLink(this);
        }
        if(null == additionalLinkProcessors) {
            this.additionalLinkProcessors = Collections.emptyList();
        } else {
            this.additionalLinkProcessors = additionalLinkProcessors;
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
        return new LinkRef(name, resource, locatorMethod, resourceMethod, additionalLinkProcessors);
    }

    /**
     * Create a LinkRef to the same LinkTarget but with different link processors.
     *
     * @param additionalLinkProcessors The alternate list of link processors
     * @return A LinkRef with the new name
     */
    public LinkRef overrideLinkProcessors(List<Class<? extends LinkProcessor>> additionalLinkProcessors)
    {
        return new LinkRef(name, resource, locatorMethod, resourceMethod, additionalLinkProcessors);
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

    public List<Class<? extends LinkProcessor>> getAdditionalLinkProcessors() {
        return additionalLinkProcessors;
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
