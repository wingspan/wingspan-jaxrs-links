package com.wingspan.platform.rs.links.jackson;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.wingspan.platform.rs.links.LinkRegistry;
import com.wingspan.platform.rs.links.LinkRegistryProvider;
import com.wingspan.platform.rs.links.TestModel;
import com.wingspan.platform.rs.links.TestResource;

/**
 * Test!
 */
public class SerializeJsonTest extends JerseyTest
{
    @Override
    protected Application configure()
    {
        LinkRegistryProvider provider = new LinkRegistryProvider();

        provider.register(TestModel.class, new LinkRegistry(TestResource.SelfLink, TestResource.ItemsLink));

        return new ResourceConfig()
            .register(TestResource.class)
            .register(JacksonFeature.class)         // Jackson support
            .register(ObjectMapperResolver.class)   // Custom object mapper for Jackson
            .register(provider);                    // LinkRegistry instance provider
    }

    @Test
    public void testSerialization()
    {
        JsonNode result = target("test").path("list").request(MediaType.APPLICATION_JSON_TYPE).get(JsonNode.class);

        System.out.println(result);
    }
}
