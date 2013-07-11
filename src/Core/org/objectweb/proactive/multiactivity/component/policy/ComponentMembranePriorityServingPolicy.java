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
package org.objectweb.proactive.multiactivity.component.policy;

import java.util.ArrayList;
import java.util.List;

import org.etsi.uri.gcm.api.control.PriorityController;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.component.body.MembraneControllerRequestFilter;
import org.objectweb.proactive.core.component.control.PAGCMLifeCycleController;
import org.objectweb.proactive.core.component.control.PAMembraneController;
import org.objectweb.proactive.multiactivity.compatibility.StatefulCompatibilityMap;
import org.objectweb.proactive.multiactivity.policy.ServingPolicy;


/**
 * Implementation of the scheduling policy to be used in a multi-active service
 * with GCM components having a {@link PAMembraneController membrane controller}
 * and a {@link PriorityController priority controller}.
 * <br>
 * With this policy, as long as the membrane is not started, only a subset of
 * non-functional requests can be served. When the membrane is started, as long
 * as the life cycle is not started, only non-functional requests can be served.
 * When both the membrane and the life cycle are started, all requests are served.
 * In any case non-functional requests cannot be executed in parallel and priorities
 * of non-functional requests are respected.
 * 
 * @author The ProActive Team
 */
public class ComponentMembranePriorityServingPolicy extends ComponentPriorityServingPolicy {

    private PAMembraneController membraneController;

    private MembraneControllerRequestFilter membraneControllerRequestFilter;

    /**
     * Creates a ComponentMembranePriorityServingPolicy.
     * 
     * @param delegate custom serving policy to wrap.
     * @param lifeCycleController The life cycle controller of the GCM component.
     * @param priorityController The priority controller of the GCM component.
     * @param membraneController The membrane controller of the GCM component.
     */
    public ComponentMembranePriorityServingPolicy(ServingPolicy delegate,
            PAGCMLifeCycleController lifeCycleController, PriorityController priorityController,
            PAMembraneController membraneController) {
        super(delegate, lifeCycleController, priorityController);

        this.membraneController = membraneController;
        this.membraneControllerRequestFilter = new MembraneControllerRequestFilter();
    }

    /**
     * Scheduling policy for GCM components with a membrane controller and a
     * priority controller.
     * <br>
     * Non-functional requests are served one by one and for the functional
     * requests it will take a request from the queue if it is compatible with
     * all executing ones and also with everyone before it in the queue. If a
     * functional request can not be taken out from the queue, the requests it
     * is invalid with are marked accordingly so that they are not retried
     * until this one is finally served.
     * 
     * @return The compatible requests to serve.
     */
    public List<Request> runPolicy(StatefulCompatibilityMap compatibility) {
        if (membraneController.getMembraneState().equals(PAMembraneController.MEMBRANE_STOPPED)) {
            List<Request> reqs = compatibility.getQueueContents();
            List<Request> ret = new ArrayList<Request>();

            if (compatibility.getNumberOfExecutingRequests() != 0) {
                // A NF request is already running, need to wait it terminates before
                // serving an other NF request
                return new ArrayList<Request>();
            }

            int nfRequestIndex = -1;

            for (int i = 0; i < reqs.size(); i++) {
                if (this.membraneControllerRequestFilter.acceptRequest(reqs.get(i))) {
                    if (this.nf3RequestFilter.acceptRequest(reqs.get(i))) {
                        // NF3 request which must be served in first
                        addRequestToRunnableRequests(reqs, i, compatibility, ret);

                        return ret;
                    } else if (nfRequestIndex == -1) {
                        // NF request (NF1 or NF2) which is the first in the queue
                        nfRequestIndex = i;
                    } else {
                        // NF request (NF1 or NF2) which is not the first in the queue
                        continue;
                    }
                } else {
                    // Requests (F or NF) that do not pass the membrane controller request filter
                    // are ignored since the membrane is stopped
                    continue;
                }
            }

            if (nfRequestIndex > -1) {
                // No NF3 request but a NF1 or NF2 request that passes the membrane controller
                // request filter and thus that can be served
                addRequestToRunnableRequests(reqs, nfRequestIndex, compatibility, ret);

                return ret;
            }

            return ret;
        } else {
            return super.runPolicy(compatibility);
        }
    }

}
