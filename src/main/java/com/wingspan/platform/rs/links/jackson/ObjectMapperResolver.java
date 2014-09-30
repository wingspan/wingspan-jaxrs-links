package com.wingspan.platform.rs.links.jackson;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This context resolver enables Jackson to serialize beans with LinksObject properties.
 *
 * @see com.wingspan.platform.rs.links.LinksObject
 */
@Provider
public class ObjectMapperResolver implements ContextResolver<ObjectMapper>
{
    @Context
    Providers _providers;
    ObjectMapper _objectMapper;

    public ObjectMapperResolver(UriBuilder baseBuilder)
    {
        _objectMapper = new ObjectMapper().registerModule(new LinksModule(_providers, baseBuilder));
    }

    public ObjectMapperResolver(UriBuilder baseBuilder, ObjectMapper mapper)
    {
        _objectMapper = mapper.registerModule(new LinksModule(_providers, baseBuilder));
    }

    @Override
    public ObjectMapper getContext(Class<?> type)
    {
        return _objectMapper;
    }
}
