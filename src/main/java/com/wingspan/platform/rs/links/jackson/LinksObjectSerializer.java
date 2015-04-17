package com.wingspan.platform.rs.links.jackson;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.wingspan.platform.rs.links.LinkBuilder;
import com.wingspan.platform.rs.links.LinkRef;
import com.wingspan.platform.rs.links.LinkRegistry;

/**
 * The serializer which uses the LinkRegistry to serialize links for a bean instance.
 *
 * @see com.wingspan.platform.rs.links.LinkRegistry
 */
public class LinksObjectSerializer extends BeanPropertyWriter
{
    private UriInfo _uriInfo;
    private ContextResolver<LinkRegistry> _resolver;

    public LinksObjectSerializer(BeanPropertyWriter base, UriInfo uriInfo, Providers providers)
    {
        super(base);

        _uriInfo = uriInfo;
        _resolver = providers.getContextResolver(LinkRegistry.class, MediaType.APPLICATION_JSON_TYPE);

        if (_resolver == null) {
            throw new IllegalStateException("A context resolver for LinkRegistry is not registered");
        }
    }

    @Override
    public void serializeAsField(Object bean, JsonGenerator jgen, SerializerProvider prov) throws Exception
    {
        LinkRegistry registry = _resolver.getContext(bean.getClass());

        if (registry == null) {	// When there's no link registry, all we can do is serialize null.
            if (this.hasNullSerializer()) {
                jgen.writeFieldName(this._name);
                this._nullSerializer.serialize(null, jgen, prov);
            }
            return;
        }

        LinkBuilder builder = LinkBuilder.create(_uriInfo.getBaseUriBuilder());

        jgen.writeFieldName(this._name);
        jgen.writeStartObject();

        for (LinkRef link : registry.getLinks()) {
            prov.defaultSerializeField(link.getName(), builder.buildUri(link, bean), jgen);
        }

        jgen.writeEndObject();
    }
}
