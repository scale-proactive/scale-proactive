package org.objectweb.proactive.annotation.multiactivity;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.objectweb.proactive.annotation.PublicAPI;

/**
 * This annotation represents a priority set to hold groups 
 * of same priority level. There can be several groups 
 * that have the same priority level.
 * 
 * @author jrochas
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@PublicAPI
public @interface Set {

    /** The names of the groups to be held in the same priority level */
    public String[] groupNames();
    
    /** The number of threads that will be reserved for executing methods that 
     * belong to groups of this set declaration */
    public int reservedThreads() default -1;
}