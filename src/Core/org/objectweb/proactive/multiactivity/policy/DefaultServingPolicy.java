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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.multiactivity.compatibility.CompatibilityManager;

import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/**
 * Default implementation of the scheduling policy to be used in a multi-active
 * service.
 * 
 * @author The ProActive Team
 */
public class DefaultServingPolicy extends ServingPolicy {

    protected static final Logger logger = ProActiveLogger.getLogger(Loggers.MULTIACTIVITY);

    protected final Set<Request> invalid = new HashSet<Request>();

    protected final Map<Request, Set<Request>> invalidates = new HashMap<Request, Set<Request>>();

    /**
     * Default scheduling policy. <br>
     * It will take a request from the queue if it is compatible with all
     * executing ones and also with everyone before it in the queue. If a
     * request can not be taken out from the queue, the requests it is invalid
     * with are marked accordingly so that they are not retried until this one
     * is finally served.
     * 
     * @return The compatible requests to serve.
     */
    @Override

    public int runPolicyOnRequest(int requestIndex, CompatibilityManager compatibility,
            List<Request> runnableRequests) {
        List<Request> requestQueue = compatibility.getQueueContents();

        int lastIndex = -2;

        if (!invalid.contains(requestQueue.get(requestIndex)) &&
            compatibility.isCompatibleWithExecuting(requestQueue.get(requestIndex)) &&
            (lastIndex = compatibility.getIndexOfLastCompatibleWith(requestQueue.get(requestIndex),
                    requestQueue.subList(0, requestIndex))) == requestIndex - 1) {
            Request r = requestQueue.get(requestIndex);
            runnableRequests.add(r);
            compatibility.addRunning(r);

            if (invalidates.containsKey(requestQueue.get(requestIndex))) {
                for (Request ok : invalidates.get(requestQueue.get(requestIndex))) {
                    invalid.remove(ok);
                }
                invalidates.remove(requestQueue.get(requestIndex));
            }

            requestQueue.remove(requestIndex);

            return --requestIndex;
        } else if (lastIndex > -2 && lastIndex < requestIndex) {
            lastIndex++;

            if (!invalidates.containsKey(requestQueue.get(lastIndex))) {
                invalidates.put(requestQueue.get(lastIndex), new HashSet<Request>());
            }

            invalidates.get(requestQueue.get(lastIndex)).add(requestQueue.get(requestIndex));
            invalid.add(requestQueue.get(requestIndex));
        }

        return requestIndex;
    }

}
