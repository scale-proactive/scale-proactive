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
     * 
     * @return
     */
    public String name();

    /**
     * Flag that shows if the methods contained in this group can run in
     * parallel or not.
     * 
     * @return
     */
    public boolean selfCompatible();

    /**
     * Class name of the common argument of all methods belonging to this group.
     * 
     * @return
     */
    public String parameter() default "";

    /**
     * Conditioning function of the self-compatibility.
     * 
     * @return
     */
    public String condition() default "";

}
