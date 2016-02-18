package com.wingspan.platform.rs.links.jackson;

import com.wingspan.platform.rs.links.LinkRef;

/**
 * Exceptions for errors occurring during link building.
 */
public class LinkBuildingException extends RuntimeException {
    protected static String buildMessage(Throwable t, LinkRef linkRef, Object bean){
        StringBuilder sb = new StringBuilder(256);
        sb.append("Error Building Link '").append(linkRef.getName()).append("' ");
        sb.append("to '").append(linkRef.getResource().getName()).append("' ");
        sb.append("for '").append(String.valueOf(bean)).append("'");
        return sb.toString();
    }
    public LinkBuildingException(Throwable t, LinkRef linkRef, Object bean) {
        super(buildMessage(t, linkRef, bean), t);
    }
}
