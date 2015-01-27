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
package org.objectweb.proactive.multiactivity.compatibility;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.proactive.core.body.request.BlockingRequestQueue;
import org.objectweb.proactive.core.body.request.Request;


/**
 * This is an implementation of the {@link StatefulCompatibilityMap} abstract class.
 * It relies on a BlockingRequestQueue (the one used by default in the body of active objects) to get the
 * list of waiting requests.
 * <br>
 * Data about currently executing requests is kept inside the class, and not read from an external source. The
 * reason for this is that compatibility checking can be sped up if we cache some data in this class.
 * @author  The ProActive Team
 */
public class CompatibilityTracker extends StatefulCompatibilityMap {

    private HashMap<MethodGroup, Set<Request>> runningGroups = new HashMap<MethodGroup, Set<Request>>();
    private Set<Request> running = new HashSet<Request>();
    private BlockingRequestQueue queue;

    public CompatibilityTracker(AnnotationProcessor annotProc, BlockingRequestQueue queue) {
        super(annotProc);

        for (MethodGroup group : getGroups()) {
            runningGroups.put(group, new HashSet<Request>());
        }
        //methods without a group
        runningGroups.put(null, new HashSet<Request>());

        this.queue = queue;
    }

    /**
     * Adds a request to the set of running requests. Called from a service when a request is started to be served.
     * @param request
     */
    public void addRunning(Request request) {
        running.add(request);
        runningGroups.get(getGroupOf(request)).add(request);
    }

    /**
     * Removes a request from the set of running requests. Called from a service when a request has finished serving.
     * @param request
     */
    public void removeRunning(Request request) {
        running.remove(request);
        runningGroups.get(getGroupOf(request)).remove(request);
    }

    @Override
    public boolean isCompatibleWithExecuting(Request r) {
        if (running.size() == 0) {
            return true;
        }

        MethodGroup reqGroup = getGroupOf(r);
        if (reqGroup == null) {
            return false;
        }

        for (MethodGroup otherGroup : runningGroups.keySet()) {
            if (runningGroups.get(otherGroup).size() > 0) {

                if (reqGroup.isComparatorDefinedFor(otherGroup)) {

                    for (Request other : runningGroups.get(otherGroup)) {
                        if (!reqGroup.isCompatible(r, otherGroup, other)) {
                            return false;
                        }
                    }

                } else if (!reqGroup.isCompatibleWith(otherGroup)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public Collection<Request> getExecutingRequests() {
        return running;
    }

    @Override
    public Request getOldestInTheQueue() {
        return queue.getOldest();
    }

    @Override
    public List<Request> getQueueContents() {
        return queue.getInternalQueue();
    }

    @Override
    public int getNumberOfExecutingRequests() {
        return running.size();
    }

}