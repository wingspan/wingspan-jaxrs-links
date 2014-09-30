package com.wingspan.platform.rs.links.jackson;

import java.util.List;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.Providers;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.wingspan.platform.rs.links.LinksObject;

/**
 * This custom module adds the bean serialization additions needed to serialize LinksObject properties.
 * It should be added to any ObjectMapper instances that
 */
public class LinksModule extends SimpleModule
{
    public LinksModule(final Providers providers, final UriBuilder baseBuilder)
    {
        super("wingspan-jaxrs-links");

        this.setSerializerModifier(new BeanSerializerModifier()
        {
            @Override
            public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
                                                             BeanDescription beanDesc,
                                                             List<BeanPropertyWriter> beanProperties)
            {
                for (int i = 0; i < beanProperties.size(); ++i) {
                    if (LinksObject.class.isAssignableFrom(beanProperties.get(i).getPropertyType())) {
                        beanProperties.set(i, new LinksObjectSerializer(beanProperties.get(i), providers, baseBuilder));
                    }
                }

                return beanProperties;
            }

        });
    }
}
