package com.wingspan.platform.rs.links;

/**
 * Base class for link processor that is limited by model types
 */
public abstract class AbstractModelSpecificLinkProcessor<TModel> implements LinkProcessor<TModel>{
    protected final Class<TModel> _modelClass;

    protected AbstractModelSpecificLinkProcessor(Class<TModel> modelClass){
        _modelClass = modelClass;
    }

    @Override
    public Class<TModel> getModelClass() {
        return _modelClass;
    }
}
