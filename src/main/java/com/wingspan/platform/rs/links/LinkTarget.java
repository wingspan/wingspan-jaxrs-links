package com.wingspan.platform.rs.links;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation goes on resource class methods to indicate they provide links for models.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LinkTarget
{
    /**
     * The name of the link, such as "self" or "edit".
     *
     * @see com.wingspan.platform.rs.links.LinkTypes
     */
    String name();

    /**
     * List of template parameters (optional)
     */
    String[] templateParams() default {};

    /**
     * When this source is on a sub-resource, this property defines the parent link name
     */
    String parentLink() default "";

    /**
     * When this source is on a sub-resource, this property defines the parent resource class
     */
    Class<?> parentResource() default Void.class;

    /**
     * A default query parameter used when generating links with a single query value (optional)
     */
    String defaultQuery() default "";

    /**
     * Additional classes that will successively process generated URIs.
     */
    Class<? extends LinkProcessor> [] linkProcessors() default {};

    /**
     * Organize links into groups for managing registrations.
     */
    Class<?>[] groups() default {};
}
