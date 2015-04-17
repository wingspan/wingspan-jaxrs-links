package com.wingspan.platform.rs.links;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

/**
 * A dummy JAX-RS resource for testing LinkBuilder and friends.
 */
@Path("/test")
@Produces(MediaType.APPLICATION_JSON)
public class TestResource
{
    public static final LinkRef ItemsLink = new LinkRef("items", TestResource.class);
    public static final LinkRef SelfLink =  new LinkRef("self", TestResource.class);
    public static final LinkRef Self2Link =  new LinkRef("self2", TestResource.class);
    public static final LinkRef FilenameLink =  new LinkRef("filename", TestResource.class);
    public static final LinkRef CommentLink = new LinkRef("comment", SubResource.class);

    @GET
    @Path("/list")
    @LinkTarget(name = "items")
    public TestModel[] getList()
    {
        return new TestModel[] { new TestModel("a"), new TestModel("b"), new TestModel("c") };
    }

    @POST
    @Path("/list")
    @Consumes(MediaType.APPLICATION_JSON)
    public TestModel[] addToList(TestModel newModel)
    {
        return new TestModel[] { new TestModel("a"), newModel };
    }

    @GET
    @Path("/{id}")
    @LinkTarget(name = LinkTypes.SELF, templateParams = "id")
    public TestModel getSomethingSpecific(@PathParam("id") String id)
    {
        return new TestModel(id);
    }

    @GET
    @Path("/{id}/more")
    @LinkTarget(name = "self2", templateParams = "id", linkProcessors = TestBeanLinkProcessor.class)
    public TestModel getSomethingElse(@PathParam("id") String id)
    {
        return new TestModel(id);
    }

    @GET
    @Path("/subrsrc")
    @LinkTarget(name = "subrsrc")
    public SubResource getSubResource()
    {
        return new SubResource();
    }

    @GET
    @Path("/{id}/{filename}")
    @LinkTarget(name = "filename", templateParams = {"id", "filename"})
    public TestModel getFileName(@PathParam("id") String id)
    {
        return new TestModel(id);
    }

    static class SubResource
    {
        @GET
        @Path("/{id}")
        @LinkTarget(name = "comment", templateParams = "id", parentLink = "subrsrc", parentResource = TestResource.class)
        public String getSubItem(@PathParam("id") String id)
        {
            return id;
        }
    }

    static class TestBeanLinkProcessor implements LinkProcessor<TestModel>
    {
        @Override
        public UriBuilder processLink(UriBuilder uriBuilder, Object[] templateValues, TestModel bean)
        {
            templateValues[0] = "linkProcessorCalled";
            return uriBuilder;
        }
    }

}
