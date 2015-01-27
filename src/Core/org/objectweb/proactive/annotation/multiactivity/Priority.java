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


/**
 * This annotation defines a priority constraint for a method name and
 * optionally the specified concrete type of parameters. The user can set
 * priority level higher than normal by using a positive increment. The priority
 * level can be adjusted over a range of -2<sup>31</sup> (the lowest) to
 * 2<sup>31</sup>-1 (the higher). Methods that satisfy no priority constraint
 * are assigned to a default (and implicit priority constraint) with priority
 * level 0.
 * <p>
 * The {@link #name()} parameter is let optional to have the possibility to
 * override the default priority constraint with a desired value for the
 * {@link #boostThreads()} attribute.
 * 
 * @author The ProActive Team
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@PublicAPI
public @interface Priority {

    /**
     * The priority level can be adjusted over a range of -2<sup>31</sup> (the
     * lowest) to 2<sup>31</sup>-1 (the higher). Methods that satisfy no
     * priority constraint are assigned to a default (and implicit priority
     * constraint) with priority level 0.
     */
    int level();

    /**
     * Defines the number of extra threads that are used to execute requests
     * that belong to this priority constraint when all available threads are
     * used to execute requests other than those that belong to this priority
     * constraint. This attribute value is useful to avoid some starvation
     * issues.
     * 
     * @return the number of extra threads that are used to execute requests
     *         that belong to this priority constraint when all available
     *         threads are used to execute requests other than those that belong
     *         to this priority constraint.
     */
    int boostThreads() default 0;

    /**
     * Only method calls with the specified name may be assigned to this
     * priority constraint.
     * 
     * @return the name of the methods that may be assigned to this priority
     *         constraint.
     */
    String name() default "";

    /**
     * Attribute used to assign to this priority constraint only method calls
     * whose parameters are of the specified types.
     * 
     * @return the type of the parameters used to filter method calls.
     */
    @SuppressWarnings("rawtypes")
    Class[] parameters() default {};

}
