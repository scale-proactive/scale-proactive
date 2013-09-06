/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2012 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.annotation.multiactivity;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.objectweb.proactive.annotation.PublicAPI;
import org.objectweb.proactive.multiactivity.limits.ThreadTracker;


/**
 * This annotation represents a method group. The compatibility rules that apply
 * on groups can be defined using the {@link DefineRules} annotation.
 * 
 * @author The ProActive Team
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@PublicAPI
public @interface Group {

    /**
     * A representative name of the group. This has to be unique for a class and
     * its predecessors.
     */
    public String name();

    /**
     * Flag that shows if the methods contained in this group can run in
     * parallel or not.
     */
    public boolean selfCompatible();
    
    /** 
     * Sets the maximum number of threads that can be occupied at the same 
     * time by the group.
     */
    public int maxThreads() default ThreadTracker.MAX_THREADS_DEFAULT;
    
    /** 
     * Sets the minimum number of threads that are reserved to execute only 
     * requests of this group. 
     */
    public int minThreads() default ThreadTracker.MIN_THREADS_DEFAULT;
    
    /**
     * Conditioning function of the self-compatibility.
     */
    public String condition() default "";

    /**
     * Class name of the common argument of all methods belonging to this group.
     */
    public String parameter() default "";
    
    /**
     * Whether requests of this group have a super priority, i.e. are executed 
     * regardless of other priorities (inserted at the head of the queue if no 
     * other super priority request lies there), and regardless of the 
     * reserved threads (a super priority request can borrow the reserved 
     * thread of another group). A super priority group cannot have a limited 
     * number of threads (not taken into account).
     */
    public boolean superPriority() default false;

}
