package org.objectweb.proactive.annotation.multiactivity;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.objectweb.proactive.annotation.PublicAPI;

/**
 * This annotation is used to define a list of 
 * {@link Priority}(ies) inside a {@link DefinePriorities} 
 * annotation.
 * 
 * @author jrochas
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@PublicAPI
public @interface PriorityOrder {

    public Priority[] value();

}
