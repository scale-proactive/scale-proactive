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

import java.util.ArrayList;
import java.util.List;

import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.multiactivity.compatibility.StatefulCompatibilityMap;


/**
 * Interface for describing the scheduling policy to be used in a multi-active
 * service.
 * 
 * @author The ProActive Team
 */
public abstract class ServingPolicy {

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
    public List<Request> runPolicy(StatefulCompatibilityMap compatibility) {
        List<Request> ret = new ArrayList<Request>();

        for (int i = 0; i < compatibility.getQueueContents().size(); i++) {
            i = this.runPolicyOnRequest(i, compatibility, ret);
        }

        return ret;
    }

    /**
     * Apply the policy on a request from the request queue.
     * 
     * @param requestIndexInRequestQueue
     *            index of the request in the request queue.
     * @param compatibility
     *            the compatibility map to perform compatibility checks, to
     *            retrieve to the request queue and thus removing requests from
     *            the request queue.
     * @param runnableRequests
     *            the requests that have been removed from the request queue
     *            should be put in this list to be passed to the executor for
     *            scheduling.
     * 
     * @return the index of the next request to check in the request queue. It
     *         should be equals to
     *         {@code requestIndexInRequestQueue - numberOfRequestsRemovedFromRequestQueue}
     *         .
     */
    public int runPolicyOnRequest(int requestIndexInRequestQueue, StatefulCompatibilityMap compatibility,
            List<Request> runnableRequests) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() +
            "#runPolicyOnRequest must be overriden if you want to use it");
    }

}
