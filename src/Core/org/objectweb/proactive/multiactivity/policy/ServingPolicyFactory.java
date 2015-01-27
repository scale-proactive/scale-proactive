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

import java.util.LinkedList;
import java.util.List;

import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.multiactivity.compatibility.StatefulCompatibilityMap;


/**
 * A factory with examples of serving policies.
 * 
 * @author The ProActive Team
 */
@Deprecated
public class ServingPolicyFactory {

    public static ServingPolicy getSingleActivityPolicy() {
        return new ServingPolicy() {

            @Override
            public List<Request> runPolicy(StatefulCompatibilityMap compatibility) {
                List<Request> ret = new LinkedList<Request>();

                if (compatibility.getNumberOfExecutingRequests() == 0 &&
                    compatibility.getQueueContents().size() > 0) {
                    ret.add(compatibility.getOldestInTheQueue());
                }

                return ret;
            }
        };
    }

    public static ServingPolicy getMultiActivityPolicy() {
        return new ServingPolicy() {

            @Override
            public List<Request> runPolicy(StatefulCompatibilityMap compatibility) {
                List<Request> ret = new LinkedList<Request>();
                Request current = compatibility.getOldestInTheQueue();

                if (current == null) {
                    return ret;
                }
                int execSize = compatibility.getExecutingRequests().size();
                if (execSize == 0) {
                    ret.add(current);
                } else {
                    List<Request> queue = compatibility.getQueueContents();
                    for (int i = 0; i < queue.size(); i++) {
                        current = queue.get(i);
                        if (i < execSize) {
                            if (compatibility.getIndexOfLastCompatibleWith(current, queue) >= i) {
                                if (compatibility.isCompatibleWithExecuting(current)) {
                                    ret.add(current);
                                }
                            }
                        } else {
                            if (compatibility.isCompatibleWithExecuting(current)) {
                                if (compatibility.getIndexOfLastCompatibleWith(current, queue) >= i) {
                                    ret.add(current);
                                }
                            }
                        }
                    }
                }

                return ret;
            }
        };
    }

    public static ServingPolicy getMultiactivePriorityPolicy(final String prioritizedMethod) {
        return new ServingPolicy() {

            @Override
            public List<Request> runPolicy(StatefulCompatibilityMap compatibility) {
                List<Request> ret = new LinkedList<Request>();
                List<Request> queue = compatibility.getQueueContents();
                for (Request r : queue) {
                    if (compatibility.isCompatibleWithExecuting(r) &&
                        r.getMethodName().equals(prioritizedMethod)) {
                        if (ret.size() > 0 && compatibility.getGroupOf(r).isSelfCompatible()) {
                            break;
                        }
                        ret.add(r);
                        return ret;
                    }
                }

                Request current = compatibility.getOldestInTheQueue();
                if (current == null) {
                    return ret;
                }
                int execSize = compatibility.getExecutingRequests().size();
                if (execSize == 0) {
                    ret.add(current);
                } else {
                    for (int i = 0; i < queue.size(); i++) {
                        current = queue.get(i);
                        if (i < execSize + ret.size()) {
                            if (compatibility.getIndexOfLastCompatibleWith(current, queue) >= i) {
                                if (compatibility.isCompatibleWithRequests(current, ret) &&
                                    compatibility.isCompatibleWithExecuting(current)) {
                                    ret.add(current);
                                    return ret;
                                }
                            }
                        } else {
                            if (compatibility.isCompatibleWithRequests(current, ret) &&
                                compatibility.isCompatibleWithExecuting(current)) {
                                if (compatibility.getIndexOfLastCompatibleWith(current, queue) >= i) {
                                    ret.add(current);
                                    return ret;
                                }
                            }
                        }
                    }
                }

                return ret;
            }
        };
    }

    public static ServingPolicy getMaxThreadMultiActivityPolicy(final int maxThreads) {
        return new ServingPolicy() {

            @Override
            public List<Request> runPolicy(StatefulCompatibilityMap compatibility) {

                if (compatibility.getExecutingRequests().size() < maxThreads) {
                    ServingPolicy maPolicy = getMultiActivityPolicy();
                    return maPolicy.runPolicy(compatibility);
                }

                return null;
            }
        };
    }

    public static ServingPolicy getGreedyMultiActivityPolicy() {
        return new ServingPolicy() {

            @Override
            public List<Request> runPolicy(StatefulCompatibilityMap compatibility) {

                List<Request> ret = new LinkedList<Request>();
                List<Request> queue = compatibility.getQueueContents();

                for (int i = 0; i < queue.size(); i++) {
                    if (compatibility.isCompatibleWithRequests(queue.get(i), compatibility
                            .getExecutingRequests())) {
                        ret.add(queue.get(i));
                        return ret;
                    }
                }

                return ret;
            }
        };
    }
}
