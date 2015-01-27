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
import org.objectweb.proactive.core.component.body.NF2RequestFilter;
import org.objectweb.proactive.core.component.body.NF3RequestFilter;
import org.objectweb.proactive.core.component.body.NFRequestFilterImpl;
import org.objectweb.proactive.core.component.control.PAGCMLifeCycleController;
import org.objectweb.proactive.multiactivity.compatibility.StatefulCompatibilityMap;
import org.objectweb.proactive.multiactivity.policy.ServingPolicy;


/**
 * Implementation of the scheduling policy to be used in a multi-active service
 * with GCM components having a {@link PriorityController priority controller}.
 * <br>
 * With this policy, as long as the life cycle is not started, only non-functional
 * requests can be served. When the life cycle controller is started, all requests are
 * served. In any case non-functional requests cannot be executed in parallel and priorities
 * of non-functional requests are respected.
 * 
 * @author The ProActive Team
 */
public class ComponentPriorityServingPolicy extends ComponentServingPolicy {

    private PAGCMLifeCycleController lifeCycleController;

    protected PriorityController priorityController;

    protected NFRequestFilterImpl nfRequestFilter;

    protected NF2RequestFilter nf2RequestFilter;

    protected NF3RequestFilter nf3RequestFilter;

    /**
     * Creates a ComponentPriorityServingPolicy.
     * 
     * @param delegate custom serving policy to wrap.
     * @param lifeCycleController The life cycle controller of the GCM component.
     * @param priorityController The priority controller of the GCM component.
     */
    public ComponentPriorityServingPolicy(ServingPolicy delegate,
            PAGCMLifeCycleController lifeCycleController, PriorityController priorityController) {
        super(delegate, lifeCycleController);
        this.lifeCycleController = lifeCycleController;
        this.priorityController = priorityController;
        this.nfRequestFilter = new NFRequestFilterImpl();
        this.nf2RequestFilter = new NF2RequestFilter(this.priorityController);
        this.nf3RequestFilter = new NF3RequestFilter(this.priorityController);
    }

    /**
     * Scheduling policy for GCM components with a priority controller.
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
        List<Request> reqs = compatibility.getQueueContents();
        List<Request> ret = new ArrayList<Request>();

        if (lifeCycleController.getFcState().equals(PAGCMLifeCycleController.STOPPED)) {
            if (compatibility.getNumberOfExecutingRequests() != 0) {
                // A NF request is already running, need to wait it terminates before
                // serving an other NF request
                return new ArrayList<Request>();
            }

            int nfRequestIndex = -1;

            for (int i = 0; i < reqs.size(); i++) {
                if (this.nf3RequestFilter.acceptRequest(reqs.get(i))) {
                    // NF3 request which must be served in first
                    addRequestToRunnableRequests(reqs, i, compatibility, ret);

                    return ret;
                } else if (this.nfRequestFilter.acceptRequest(reqs.get(i))) {
                    // NF request (NF1 or NF2)
                    if (nfRequestIndex == -1) {
                        // NF request (NF1 or NF2) which is the first in the queue
                        nfRequestIndex = i;
                    } else {
                        // NF request (NF1 or NF2) which is not the first in the queue
                        continue;
                    }
                } else {
                    // F requests are ignored since the component is stopped
                    continue;
                }
            }

            if (nfRequestIndex > -1) {
                // No NF3 request but a NF1 or NF2 request that can be served
                addRequestToRunnableRequests(reqs, nfRequestIndex, compatibility, ret);

                return ret;
            }
        } else {
            boolean nf1Request = false;
            boolean nf2Request = false;
            boolean nf3Request = false;
            int nfRequestIndex = -1;

            for (int i = 0; i < reqs.size(); i++) {
                if (this.nfRequestFilter.acceptRequest(reqs.get(i))) {
                    // NF request
                    if (this.nf3RequestFilter.acceptRequest(reqs.get(i))) {
                        // NF3 request that must be served in first
                        nf3Request = true;
                        nfRequestIndex = i;
                        break;
                    } else if (this.nf2RequestFilter.acceptRequest(reqs.get(i)) && !nf1Request && !nf2Request) {
                        // NF2 request not preceded by an another NF1 or NF2 request
                        nf2Request = true;
                        nfRequestIndex = i;
                    } else if (!nf1Request && !nf2Request) {
                        // NF1 request not preceded by an another NF1 or NF2 request
                        nf1Request = true;
                        nfRequestIndex = i;
                    } else {
                        // NF request (NF1 or NF2) which is not the first in the queue
                        continue;
                    }
                }
            }

            if (nf3Request || nf2Request) {
                if (compatibility.getNumberOfExecutingRequests() != 0) {
                    // Requests are already running, need to wait they terminate
                    return new ArrayList<Request>();
                } else {
                    // The NF3 or NF2 request can be served
                    addRequestToRunnableRequests(reqs, nfRequestIndex, compatibility, ret);

                    return ret;
                }
            }

            for (int i = 0; i < reqs.size(); i++) {
                if (this.nfRequestFilter.acceptRequest(reqs.get(i))) {
                    // NF1 request
                    if ((compatibility.getNumberOfExecutingRequests() == 0) && (ret.size() == 0)) {
                        // The NF1 request can be served
                        addRequestToRunnableRequests(reqs, nfRequestIndex, compatibility, ret);

                        return ret;
                    } else {
                        break;
                    }
                }

                i = this.runPolicyOnRequest(i, compatibility, ret);
            }
        }

        return ret;
    }

}
