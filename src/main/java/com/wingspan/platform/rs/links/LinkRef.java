package com.wingspan.platform.rs.links;

/**
 * A reference to a link on a resource class.
 *
 * @see com.wingspan.platform.rs.links.LinkTarget
 */
public class LinkRef
{
    String   name;
    Class<?> resource;

    public static final LinkRef NONE = new LinkRef("", Void.class);

    public LinkRef(String name, Class<?> resource)
    {
        this.name = name;
        this.resource = resource;
    }

    public String getName()
    {
        return name;
    }

    public Class<?> getResource()
    {
        return resource;
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

        return name.equals(linkRef.name) && resource.equals(linkRef.resource);
    }

    @Override
    public int hashCode()
    {
        int result = name.hashCode();
        result = 31 * result + resource.hashCode();
        return result;
    }
}
