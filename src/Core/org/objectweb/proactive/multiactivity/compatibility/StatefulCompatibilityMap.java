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
import java.util.List;

import org.objectweb.proactive.core.body.request.Request;


/**
 * This is an extension to the {@link CompatibilityMap}, and it incorporates information
 * about the state of the scheduler. It facilitates checking compatibility of methods with
 * the ones in the waiting queue, or the ones that are currently executing.
 * <br>
 * This is an abstract class and it should be implemented for each service in part, so it knows how to 
 * access the queue etc.
 * @author The ProActive Team
 *
 */
public abstract class StatefulCompatibilityMap extends CompatibilityMap {

    public StatefulCompatibilityMap(AnnotationProcessor annotProc) {
        super(annotProc);
    }

    public StatefulCompatibilityMap(Class<?> clazz) {
        super(clazz);
    }

    public abstract void addRunning(Request request);

    /*
     * Returns the set of methods which are currently executing. Even if 
     * a method is executing in multiple instances, it will appear only 
     * once in this set.
     * @return
     */
    //	public abstract Set<String> getExecutingMethodNameSet();
    /*
     * Returns the list of methods which are currently executing. If a method
     * is executing in multiple instances, it will appear multiple times in
     * this list.
     * @return
     */
    //	public abstract List<String> getExecutingMethodNames();
    /**
     * Returns the list of requests which are currently executing.
     * @return
     */
    public abstract Collection<Request> getExecutingRequests();

    /**
     * Returns the number of executing requests in the service.
     * @return
     */
    public abstract int getNumberOfExecutingRequests();

    /*
     * Returns only the instances of a given method name which are
     * executing
     * @param name
     * @return
     */
    //	public abstract List<Request> getExecutingRequestsFor(String method);
    /**
     * Gives the content of the request queue of the scheduler.
     * Elements are sorted in descending order of their age
     * @return
     */
    public abstract List<Request> getQueueContents();

    /**
     * Returns the first element of the queue, or null in case
     * the queue is empty 
     * @return
     */
    public abstract Request getOldestInTheQueue();

    /**
     * Returns true if the given request can be run in parallel with all 
     * methods that are currently executing. 
     * @param request
     * @return
     */
    public abstract boolean isCompatibleWithExecuting(Request request);

    /*
     * Returns true if the given method can be run in parallel with all 
     * methods that are currently executing. 
     * @param method
     * @return
     */
    //	public abstract boolean isCompatibleWithExecuting(String method);
}
