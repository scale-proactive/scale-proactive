package org.objectweb.proactive.annotation.multiactivity;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.objectweb.proactive.annotation.PublicAPI;

/**
 * This annotation represents a priority level. There can
 * be several groups that have the same priority level.
 * 
 * @author jrochas
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@PublicAPI
public @interface Priority {

    /**
     * A representative name of the group. This has to be unique for a class and
     * its predecessors.
     * 
     * @return
     */
    public String[] groupNames();
}