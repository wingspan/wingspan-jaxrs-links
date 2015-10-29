package com.wingspan.platform.rs.links;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import static com.wingspan.platform.rs.links.TestModel.CommentModel;

/**
 * A generic sub-resource that could be shared by multiple parent resources.
 */
public class TestSubResource
{
    public static final LinkRef CommentLink = new LinkRef("comment", TestSubResource.class);

    CommentModel[] comments = new CommentModel[] {
            new CommentModel("1000"),
            new CommentModel("1001"),
            new CommentModel("1002")
    };

    @GET
    public CommentModel[] listSubItems()
    {
        return comments;
    }

    @GET
    @Path("/{id}")
    @LinkTarget(name = "comment", templateParams = "id")
    public CommentModel getComment(@PathParam("id") String id)
    {
        return new CommentModel(id);
    }

}
