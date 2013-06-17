package org.objectweb.proactive.annotation.multiactivity;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.objectweb.proactive.annotation.PublicAPI;


/**
 * This annotation represents a priority set to hold groups 
 * of same priority level.
 * 
 * @author jrochas
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@PublicAPI
public @interface Priority {
	
	/** The names of the groups belonging to the same priority level */
	String[] groupNames();
	
	/** The priority level defined through an integer value */
	int level() default 0;
	
}
