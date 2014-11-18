package com.wingspan.platform.rs.links.jackson;

import java.util.List;

import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Providers;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
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
    public LinksModule(final UriInfo uriInfo, final Providers providers)
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
                        beanProperties.set(i, new LinksObjectSerializer(beanProperties.get(i), uriInfo, providers));
                    }
                }

                return beanProperties;
            }

        });

        this.setDeserializerModifier(new BeanDeserializerModifier()
        {
            @Override
            public BeanDeserializerBuilder updateBuilder(DeserializationConfig config,
                                                         BeanDescription beanDesc,
                                                         BeanDeserializerBuilder builder)
            {
                for (BeanPropertyDefinition prop : beanDesc.findProperties()) {
                    // Mark the "links" property as ignorable, because links are read-only values.
                    if (prop.hasGetter() && LinksObject.class.isAssignableFrom(prop.getGetter().getRawReturnType())) {
                        builder.addIgnorable(prop.getName());
                    }
                }
                return builder;
            }
        });
    }
}
