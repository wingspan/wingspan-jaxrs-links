package com.wingspan.platform.rs.links.jackson;

import javax.ws.rs.core.Context;
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
    ObjectMapper _objectMapper;

    public ObjectMapperResolver(@Context Providers providers)
    {
        _objectMapper = new ObjectMapper().registerModule(new LinksModule(providers));
    }

    @Override
    public ObjectMapper getContext(Class<?> type)
    {
        return _objectMapper;
    }
}
