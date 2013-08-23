package org.objectweb.proactive.annotation.multiactivity;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.objectweb.proactive.annotation.PublicAPI;

/**
 * This annotation represents a priority set to hold group names of same 
 * priority level. There can be several groups that have the same priority 
 * level.
 * 
 * @author The ProActive team
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@PublicAPI
public @interface Set {

    /** The names of the groups to be held in the same priority level */
    public String[] groupNames();
    
}