package com.wingspan.platform.rs.links;

/**
 * Base class for Link processor that applies to any model.
 */
public abstract class AbstractLinkProcessor implements LinkProcessor<Object>{

    @Override
    public Class<Object> getModelClass() {
        return Object.class;
    }
}
