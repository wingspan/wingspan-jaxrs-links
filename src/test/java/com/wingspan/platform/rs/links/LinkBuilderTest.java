package com.wingspan.platform.rs.links;

import java.net.URI;
import java.net.URISyntaxException;
import javax.ws.rs.core.UriBuilder;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for LinkBuilder
 */
public class LinkBuilderTest
{
    @Test
    public void testBuilderFor()
    {
        String basePath = "/LinkBuilderTest/testBuilderFor";

        UriBuilder baseBuilder = UriBuilder.fromPath(basePath);
        LinkBuilder linkBuilder = new LinkBuilder(baseBuilder);

        UriBuilder builder = linkBuilder.newUriBuilder(TestResource.ItemsLink);
        assertNotNull(builder);
        assertEquals(basePath + "/test/list", builder.build().toString());

        try
        {
            linkBuilder.newUriBuilder(new LinkRef("badname", TestResource.class));
            fail("Should not return null for invalid names");
        }
        catch (RuntimeException e)
        {
            assertTrue(e instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testBuildUriWithBean()
    {
        String basePath = "/LinkBuilderTest/testBuildUriWithBean";

        UriBuilder baseBuilder = UriBuilder.fromPath(basePath);
        LinkBuilder linkBuilder = new LinkBuilder(baseBuilder);
        TestModel bean = new TestModel("123456789");

        URI beanUrl = linkBuilder.buildUri(TestResource.SelfLink, bean);
        assertNotNull(beanUrl);
        assertEquals(basePath + "/test/123456789", beanUrl.toString());

        // And if you don't pass a bean to a URI that has template arguments...
        try {
            linkBuilder.buildUri(TestResource.SelfLink);
            fail();
        }
        catch (RuntimeException e) {
            assertTrue(e instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testLinkProcessors()
    {
        String basePath = "/LinkBuilderTest/testLinkProcessors";

        UriBuilder baseBuilder = UriBuilder.fromPath(basePath);
        LinkBuilder linkBuilder = new LinkBuilder(baseBuilder);
        TestModel bean = new TestModel("123456789");

        URI beanUrl = linkBuilder.buildUri(TestResource.Self2Link, bean);
        assertNotNull(beanUrl);
        assertEquals(basePath + "/test/linkProcessorCalled/more", beanUrl.toString());

    }

    @Test
    public void testSubResources()
    {
        String basePath = "/LinkBuilderTest/testSubResources";

        UriBuilder baseBuilder = UriBuilder.fromPath(basePath);
        LinkBuilder linkBuilder = new LinkBuilder(baseBuilder);
        TestModel bean = new TestModel("123456789");

        URI beanUrl = linkBuilder.buildUri(TestResource.SubRsrcLink, bean);
        assertNotNull(beanUrl);
        assertEquals(basePath + "/test/subrsrc", beanUrl.toString());

        TestModel.CommentModel comment = new TestModel.CommentModel("1000");

        beanUrl = linkBuilder.buildUri(TestResource.CommentLink, comment);
        assertNotNull(beanUrl);
        assertEquals(basePath + "/test/1000/comments/1000", beanUrl.toString());
    }

    @Test
    public void testFilenameEncoding()
        throws URISyntaxException
    {
        String basePath = "/LinkBuilderTest/testFileNameEncoding";

        UriBuilder baseBuilder = UriBuilder.fromPath(basePath);
        LinkBuilder linkBuilder = new LinkBuilder(baseBuilder);
        TestModel testBean = new TestModel("123456789");

        URI beanUrl = linkBuilder.buildUri(TestResource.FilenameLink, testBean);

        assertFalse(beanUrl.toString().endsWith(testBean.getFilename()));
        assertFalse(beanUrl.toString().contains("%252F"));
        assertFalse(beanUrl.toString().contains("%5C"));
    }
}
