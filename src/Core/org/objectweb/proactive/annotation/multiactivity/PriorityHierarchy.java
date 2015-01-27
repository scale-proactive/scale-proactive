package org.objectweb.proactive.annotation.multiactivity;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.objectweb.proactive.annotation.PublicAPI;

/**
 * This annotation is used to define a list of {@link PrioritySet}s inside a 
 * {@link DefinePriorities} annotation. The order in which the 
 * {@link PrioritySet}s are defined is significant: groups in successive {@link PrioritySet} 
 * annotations have a decreasing priority.
 * 
 * @author The ProActive Team
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@PublicAPI
public @interface PriorityHierarchy {

	/** Represents a priority order */
    public PrioritySet[] value();

}
