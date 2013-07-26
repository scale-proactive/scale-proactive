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
package org.objectweb.proactive.multiactivity.policy;

import java.util.List;

import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.multiactivity.compatibility.CompatibilityTracker;


/**
 * Interface for describing the scheduling policy to be used in a multi-active
 * service.
 * 
 * @author The ProActive Team
 */
public interface ServingPolicy {

    /**
     * This method will decide which methods get to run given the current state
     * of the scheduler and the relation between methods. <br>
     * <i>IMPORTANT:</i> While executing a policy the state of the queue and the
     * running set is guaranteed not to change. <br>
     * Please also note this is up to the person that defines the serving policy
     * to add requests to the running with a call to
     * {@link StatefulCompatibilityMap#addRunning(Request)} and to remove the
     * requests that are returned with this method from the compatibility map
     * passed into parameter through a call to remove on
     * {@link StatefulCompatibilityMap#getQueueContents()}. This allows to use
     * some caches and thus to remove requests from the compatibility map by
     * index or by value.
     * 
     * @param compatibilityMap
     * 
     * @return a sublist of the requests that can be executed in parallel.
     */
    public List<Request> runCompatibilityPolicy(CompatibilityTracker compatibility);

}
