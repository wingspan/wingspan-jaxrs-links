package com.wingspan.platform.rs.links.jackson;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.wingspan.platform.rs.links.LinkRegistry;
import com.wingspan.platform.rs.links.LinkRegistryProvider;
import com.wingspan.platform.rs.links.TestModel;
import com.wingspan.platform.rs.links.TestResource;

import static org.junit.Assert.*;

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

        assertEquals("http://localhost:9998/test/a", result.at("/0/links/self").textValue());
        assertEquals("http://localhost:9998/test/b", result.at("/1/links/self").textValue());
        assertEquals("http://localhost:9998/test/c", result.at("/2/links/self").textValue());
    }

    @Test
    public void testDeserialization()
    {
        /* By default, link properties should not be involved in deserialization at all */
        JsonNode result = target("test").path("list").request(MediaType.APPLICATION_JSON_TYPE).get(JsonNode.class);

        JsonNode model = result.get(1);

        // Post the JSON result to a service that expects the model
        Response response = target("test").path("list").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(model, MediaType.APPLICATION_JSON_TYPE));

        assertEquals(200, response.getStatus());
    }
}
