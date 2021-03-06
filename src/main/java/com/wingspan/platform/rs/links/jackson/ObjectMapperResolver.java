package com.wingspan.platform.rs.links.jackson;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This context resolver enables Jackson to serialize beans with LinksObject properties.
 * If your application has its own ObjectMapper resolver, you can use this code as an example.
 *
 * @see com.wingspan.platform.rs.links.LinksObject
 */
@Provider
public class ObjectMapperResolver implements ContextResolver<ObjectMapper>
{
    ObjectMapper _objectMapper;

    public ObjectMapperResolver(@Context UriInfo uriInfo, @Context Providers providers)
    {
        _objectMapper = new ObjectMapper().registerModule(new LinksModule(uriInfo, providers));
    }

    @Override
    public ObjectMapper getContext(Class<?> type)
    {
        return _objectMapper;
    }
}
