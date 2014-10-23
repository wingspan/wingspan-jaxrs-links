package com.wingspan.platform.rs.links;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

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

    LinkRegistry combineWith(LinkRegistry registry)
    {
        HashSet<LinkRef> newLinks = new HashSet<>(this.links);
        newLinks.addAll(registry.links);

        return new LinkRegistry(newLinks);
    }
}
