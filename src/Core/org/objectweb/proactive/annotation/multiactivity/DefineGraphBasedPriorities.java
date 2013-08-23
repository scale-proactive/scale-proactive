package org.objectweb.proactive.annotation.multiactivity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.objectweb.proactive.annotation.PublicAPI;

/**
 * This annotation is used to define a list of 
 * {@link PriorityOrder}s at the header of a
 * class, to model priorities as a dependency graph.
 * 
 * @author The ProActive Team
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@PublicAPI
public @interface DefineGraphBasedPriorities {

	/** Represents an union of priority orders */
    public PriorityOrder[] value();

}