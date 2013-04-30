package org.objectweb.proactive.annotation.multiactivity;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.objectweb.proactive.annotation.PublicAPI;

/**
 * This annotation represents a method group. The compatibility rules that apply
 * on groups can be defined using the {@link DefineRules} annotation.
 * 
 * @author jrochas
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@PublicAPI
public @interface GroupPriority {

    /**
     * A representative name of the group. This has to be unique for a class and
     * its predecessors.
     * 
     * @return
     */
    public String[] groupNames();
}