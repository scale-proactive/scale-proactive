package org.objectweb.proactive.annotation.multiactivity;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.objectweb.proactive.annotation.PublicAPI;

/**
 * This annotation is used to define a list of {@link Set}s inside
 * a {@link DefineGraphBasedPriorities} annotation. The order in 
 * which the {@link Set}s are defined is significant: groups in 
 * successive {@link Set} annotations have a decreasing priorities
 * 
 * @author jrochas
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@PublicAPI
public @interface PriorityOrder {

    public Set[] value();

}
