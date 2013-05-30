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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.multiactivity.compatibility.StatefulCompatibilityMap;

/**
 * Interface for describing the scheduling policy to be used in a multi-active
 * service.
 * 
 * @author The ProActive Team
 */
public class DefaultServingPolicy implements ServingPolicy {

    private HashSet<Request> invalid = new HashSet<Request>();

    private HashMap<Request, Set<Request>> invalidates =
            new HashMap<Request, Set<Request>>();

    /**
     * Default scheduling policy. <br>
     * It will take a request from the queue if it is compatible with all
     * executing ones and also with everyone before it in the queue. If a
     * request can not be taken out from the queue, the requests it is invalid
     * with are marked accordingly so that they are not retried until this one
     * is finally served.
     * 
     * @return compatible requests to serve.
     */
    public List<Request> runPolicy(StatefulCompatibilityMap compatibility) {
        List<Request> reqs = compatibility.getQueueContents();
        List<Request> ret = new ArrayList<Request>();

        int i, lastIndex;
        for (i = 0; i < reqs.size(); i++) {
            lastIndex = -2;
            if (!invalid.contains(reqs.get(i))
                    && compatibility.isCompatibleWithExecuting(reqs.get(i))
                    && (lastIndex =
                            compatibility.getIndexOfLastCompatibleWith(
                                    reqs.get(i), reqs.subList(0, i))) == i - 1) {
                Request r = reqs.get(i);
                ret.add(r);

                if (invalidates.containsKey(reqs.get(i))) {
                    for (Request ok : invalidates.get(reqs.get(i))) {
                        invalid.remove(ok);
                    }
                    invalidates.remove(reqs.get(i));
                }

                reqs.remove(i);
                i--;

            } else if (lastIndex > -2 && lastIndex < i) {
                lastIndex++;
                if (!invalidates.containsKey(reqs.get(lastIndex))) {
                    invalidates.put(reqs.get(lastIndex), new HashSet<Request>());
                }

                invalidates.get(reqs.get(lastIndex)).add(reqs.get(i));
                invalid.add(reqs.get(i));
            }
        }

        return ret;
    }

}
