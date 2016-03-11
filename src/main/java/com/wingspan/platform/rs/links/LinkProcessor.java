/*
 * Copyright 2002-2013 by Wingspan Technology, Inc.,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Wingspan Technology, Inc. ("Confidential Information"). You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Wingspan Technology.
 *
 */
package com.wingspan.platform.rs.links;

import javax.ws.rs.core.UriBuilder;

/**
 * Defines a hook into the serializer for links.
 */
public interface LinkProcessor<TModel>
{
    Class<TModel> getModelClass();

    /**
     * Returning both the uriBuilder and template values allow link processors to modify said template values.
     */
    public UriBuilder processLink(UriBuilder uriBuilder, Object[] templateValues, TModel bean);
}
