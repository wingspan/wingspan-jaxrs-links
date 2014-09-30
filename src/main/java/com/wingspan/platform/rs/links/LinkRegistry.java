package com.wingspan.platform.rs.links;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * A container of registrations for accessing links.
 */
public class LinkRegistry
{
    private Collection<LinkRef> links;

    public LinkRegistry(LinkRef... links)
    {
        this.links = Collections.unmodifiableCollection(Arrays.asList(links));
    }

    public LinkRegistry(Collection<LinkRef> links)
    {
        this.links = Collections.unmodifiableCollection(links);
    }

    public Iterable<LinkRef> getLinks()
    {
        return links;
    }

    public LinkRegistry combineWith(LinkRegistry registry)
    {
        HashSet<LinkRef> newLinks = new HashSet<>(this.links);
        newLinks.addAll(registry.links);

        return new LinkRegistry(newLinks);
    }
}
