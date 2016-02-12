package com.wingspan.platform.rs.links;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * This provider is used by serialization libraries to build the links when a model is serialized
 */
@Provider
public class LinkRegistryProvider<T> implements ContextResolver<LinkRegistry>
{
    private Map<Class<?>, LinkRegistry> linkRegistryMap;

    public LinkRegistryProvider()
    {
        linkRegistryMap = Collections.emptyMap();
    }

    @Override
    public LinkRegistry getContext(Class<?> type)
    {
        if (type == Object.class) {
            return null;
        }
        if (linkRegistryMap.containsKey(type)) {
            return linkRegistryMap.get(type);
        }

        return getContext(type.getSuperclass());
    }

    public void register(Class<? extends T> modelType, LinkRegistry registry)
    {
        LinkRegistry oldRegistry = getContext(modelType);
        Map<Class<?>, LinkRegistry> newMap = new HashMap<>(linkRegistryMap);

        if (oldRegistry != null) {
            registry = oldRegistry.combineWith(registry);
        }

        newMap.put(modelType, registry);
        this.linkRegistryMap = Collections.unmodifiableMap(newMap);
    }

    public void register(Class<? extends T> modelType, LinkRegistry ...registries)
    {
        for (LinkRegistry registry : registries) {
            register(modelType, registry);
        }
    }
}
