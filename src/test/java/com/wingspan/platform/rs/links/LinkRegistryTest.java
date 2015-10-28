package com.wingspan.platform.rs.links;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for LinksObject
 */
public class LinkRegistryTest
{
    static class SubModel extends TestModel
    {
        public SubModel(String id)
        {
            super(id);
        }
    }

    LinkRegistryProvider provider = new LinkRegistryProvider();

    @Test
    public void testRegistry()
    {
        provider.register(TestModel.class, new LinkRegistry(TestResource.SelfLink, TestResource.ItemsLink));

        TestModel model = new TestModel("123456789");

        for (LinkRef link : provider.getContext(model.getClass()).getLinks()) {
            assertEquals(TestResource.class, link.getResource());
        }

        // Test that adding additional links provided by other resources doesn't clobber earlier registrations
        provider.register(TestModel.class, new LinkRegistry(TestResource.CommentLink));
        List<String> names = new ArrayList<>();

        for (LinkRef link : provider.getContext(model.getClass()).getLinks()) {
            names.add(link.getName());
        }

        assertEquals(3, names.size());
        assertTrue(names.contains("items") && names.contains("self"));  // Links from the first registerModel() call
        assertTrue(names.contains("comment"));                          // The link from the second registerModel() call

        // Test that a model's subtype will inherit the link registration
        SubModel subModel = new SubModel("bye");

        assertNotNull("Failed to find subtype links", provider.getContext(subModel.getClass()));
    }

    @Test
    public void testGrouping()
    {
        LinkRegistry registry1 = LinkRegistry.fromResource(TestResource.class);

        assertNotNull(registry1);
        assertEquals(6, registry1.getLinks().size());

        LinkRegistry registry2 = LinkRegistry.fromResource(TestResource.class, null);

        assertEquals(registry1.getLinks().size(), registry2.getLinks().size());

        LinkRegistry registry3 = LinkRegistry.fromResource(TestResource.class, TestGroups.Group3.class);

        assertEquals(3, registry3.getLinks().size());
    }
}
