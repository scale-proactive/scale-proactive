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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.objectweb.proactive.annotation.PublicAPI;
import org.objectweb.proactive.multiactivity.compatibility.AnnotationProcessor;


/**
 * This annotation can be used to express parallel compatibility between groups.
 * It is to be used inside a {@link DefineRules} annotation, and defines a set
 * of method groups (identified by name) which can run in parallel. Optionally,
 * a conditioning function can be defined (see {@link AnnotationProcessor} for
 * details on this function).
 * 
 * @author The ProActive Team
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@PublicAPI
public @interface Compatible {

    /**
     * List of group names that can run in parallel.
     * 
     * @return
     */
    public String[] value();

    /**
     * Conditioning function of the compatibility rule.
     * 
     * @return
     */
    public String condition() default "";

}
