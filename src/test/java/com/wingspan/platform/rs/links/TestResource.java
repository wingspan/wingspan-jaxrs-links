package com.wingspan.platform.rs.links;

import java.util.Collections;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
    public static final LinkRef Self2LinkWithLinkProcessor =  new LinkRef("self2", TestResource.class, Collections.singletonList(LinkRefLinkProcessor.class));
    public static final LinkRef MaybeLink =  new LinkRef("maybe", TestResource.class);
    public static final LinkRef FilenameLink =  new LinkRef("filename", TestResource.class);
    public static final LinkRef SubRsrcLink = new LinkRef("subrsrc", TestResource.class);

    // Since comments are a sub-resource, we need to create a reference that indicates how to chain them.
    public static final LinkRef CommentLink =
            TestSubResource.CommentLink.fromParentMethod(TestResource.class, "getSubResourceWithID");

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
    @LinkTarget(name = LinkTypes.SELF, templateParams = "id", groups = TestGroups.Group1.class)
    public TestModel getSomethingSpecific(@PathParam("id") String id)
    {
        return new TestModel(id);
    }

    @GET
    @Path("/{id}/more")
    @LinkTarget(name = "self2", templateParams = "id", groups = TestGroups.Group2.class,
        linkProcessors = TestBeanLinkProcessor.class)
    public TestModel getSomethingElse(@PathParam("id") String id)
    {
        return new TestModel(id);
    }

    @GET
    @Path("/{id}/maybe")
    @LinkTarget(name = "maybe", templateParams = "id", condition = TestModel.HasFile.class)
    public TestModel getMaybe(@PathParam("id") String id)
    {
        return new TestModel(id);
    }

    @Path("/subrsrc")
    @LinkTarget(name = "subrsrc")
    public TestSubResource getSubResource()
    {
        return new TestSubResource();
    }

    @GET
    @Path("/{id}/{filename}")
    @LinkTarget(name = "filename", templateParams = {"id", "filename"}, groups = TestGroups.Group1.class)
    public TestModel getFileName(@PathParam("id") String id)
    {
        return new TestModel(id);
    }

    @Path("/{id}/comments")
    public TestSubResource getSubResourceWithID(@PathParam("id") String id)
    {
        return new TestSubResource();
    }

    static class TestBeanLinkProcessor implements LinkProcessor<TestModel>
    {
        @Override
        public Class<TestModel> getModelClass() {
            return TestModel.class;
        }

        @Override
        public UriBuilder processLink(UriBuilder uriBuilder, Object[] templateValues, TestModel bean)
        {
            templateValues[0] = "linkProcessorCalled";
            return uriBuilder;
        }
    }

    static class LinkRefLinkProcessor implements LinkProcessor<TestModel>
    {
        @Override
        public Class<TestModel> getModelClass() {
            return TestModel.class;
        }

        @Override
        public UriBuilder processLink(UriBuilder uriBuilder, Object[] templateValues, TestModel bean)
        {
            templateValues[0] = "linkProcessorFromLinkRefCalled";
            return uriBuilder;
        }
    }



}
