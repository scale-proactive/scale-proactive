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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.multiactivity.compatibility.StatefulCompatibilityMap;
import org.objectweb.proactive.multiactivity.execution.RequestExecutor;


/**
 * Default implementation of the scheduling policy to be used in a multi-active
 * service.
 * 
 * @author The ProActive Team
 */
public class DefaultServingPolicy extends ServingPolicy {

    protected static final Logger log = ProActiveLogger.getLogger(Loggers.MULTIACTIVITY);

    // protected final Set<Request> invalid = new HashSet<Request>();

    // protected final Map<Request, Set<Request>> invalidates =
    // new HashMap<Request, Set<Request>>();

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
    public int runPolicyOnRequest(int requestIndex, StatefulCompatibilityMap compatibility,
            List<Request> runnableRequests) {
        List<Request> requestQueue = compatibility.getQueueContents();
        Request request = requestQueue.get(requestIndex);

        // int lastIndex = -2;
        // boolean valid = !invalid.contains(requestQueue.get(requestIndex));
        // (lastIndex =
        // compatibility.getIndexOfLastCompatibleWith(
        // requestQueue.get(requestIndex),
        // requestQueue.subList(0, requestIndex))) == requestIndex - 1;

        boolean compatibleWithExecuting = compatibility.isCompatibleWithExecuting(requestQueue
                .get(requestIndex));

        boolean compatibleWithPreceding;

        boolean runnable = compatibleWithExecuting &&
            (compatibleWithPreceding = isCompatibleWithPreceding(compatibility, request, requestIndex));

        if (log.isDebugEnabled()) {
            // compute the value in any case when debug is enabled
            compatibleWithPreceding = isCompatibleWithPreceding(compatibility, request, requestIndex);

            StringBuilder msg = new StringBuilder();
            msg.append("Is ");
            msg.append(RequestExecutor.toString(requestQueue.get(requestIndex)));
            msg.append(" runnable? ");
            msg.append(runnable);
            msg.append(", compatibleWithExecuting? ");
            msg.append(compatibleWithExecuting);
            msg.append(", compatibleWithPreceding? ");
            msg.append(compatibleWithPreceding);
            msg.append(", executing=[");
            msg.append(toString(compatibility.getExecutingRequests()));
            msg.append("], preceding=[");
            msg.append(toString(requestQueue.subList(0, requestIndex)));
            msg.append("]");

            log.debug(msg.toString());
        }

        if (runnable) {
            runnableRequests.add(request);
            compatibility.addRunning(request);

            // if (invalidates.containsKey(requestQueue.get(requestIndex))) {
            // for (Request ok :
            // invalidates.get(requestQueue.get(requestIndex))) {
            // invalid.remove(ok);
            // }
            // invalidates.remove(requestQueue.get(requestIndex));
            // }

            requestQueue.remove(requestIndex);

            return --requestIndex;
        }
        // else if (lastIndex > -2 && lastIndex < requestIndex) {
        // lastIndex++;
        //
        // if (!invalidates.containsKey(requestQueue.get(lastIndex))) {
        // invalidates.put(
        // requestQueue.get(lastIndex), new HashSet<Request>());
        // }
        //
        // invalidates.get(requestQueue.get(lastIndex)).add(
        // requestQueue.get(requestIndex));
        // invalid.add(requestQueue.get(requestIndex));
        // }

        return requestIndex;
    }

    private final boolean isCompatibleWithPreceding(StatefulCompatibilityMap compatibility, Request request,
            int requestIndex) {
        List<Request> requestQueue = compatibility.getQueueContents();

        return compatibility.getIndexOfLastCompatibleWith(requestQueue.get(requestIndex), requestQueue
                .subList(0, requestIndex)) == requestIndex - 1;
    }

    public static String toString(Collection<Request> requests) {
        StringBuilder result = new StringBuilder();

        Iterator<Request> it = requests.iterator();

        while (it.hasNext()) {
            result.append(RequestExecutor.toString(it.next()));

            if (it.hasNext()) {
                result.append(", ");
            }
        }
        return result.toString();
    }

}
