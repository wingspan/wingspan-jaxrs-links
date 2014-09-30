package com.wingspan.platform.rs.links;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import javax.ws.rs.core.UriBuilder;

/**
 * A container of registrations for accessing links. This registry can apply to one or more model types,
 * depending on how context resolution is implemented. Typically, each model type has its own registry instance.
 *
 * @see com.wingspan.platform.rs.links.LinkRegistryProvider
 */
public class LinkRegistry
{
    private URI baseURI;
    private Collection<LinkRef> links;

    /**
     * Create a new registry for links rooted at the given URI.
     *
     * @param baseURI URI that will initialize the UriBuilder
     * @param links Link references in this registry
     */
    public LinkRegistry(URI baseURI, LinkRef... links)
    {
        this(baseURI, Arrays.asList(links));
    }

    /**
     * Create a new registry for links rooted at the given URI.
     *
     * @param baseURI URI that will initialize the UriBuilder
     * @param links Link references in this registry
     */
    public LinkRegistry(URI baseURI, Collection<LinkRef> links)
    {
        this.baseURI = baseURI;
        this.links = Collections.unmodifiableCollection(links);
    }

    public UriBuilder getBaseBuilder()
    {
        // Since the UriBuilder is mutable, it's safest to just return a new one each time.
        return UriBuilder.fromUri(baseURI);
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

        return new LinkRegistry(baseURI, newLinks);
    }
}
