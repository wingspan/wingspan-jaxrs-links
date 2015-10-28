package com.wingspan.platform.rs.links;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Created by agoodale on 10/28/15.
 */
public class TestSubResource
{
    @GET
    @Path("/{id}")
    @LinkTarget(name = "comment", templateParams = "id", parentLink = "subrsrc", parentResource = TestResource.class)
    public String getSubItem(@PathParam("id") String id)
    {
        return id;
    }

}
