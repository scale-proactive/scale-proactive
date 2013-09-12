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
package org.objectweb.proactive.multiactivity;

import java.util.LinkedList;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.Service;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.multiactivity.compatibility.AnnotationProcessor;
import org.objectweb.proactive.multiactivity.compatibility.CompatibilityManager;
import org.objectweb.proactive.multiactivity.compatibility.CompatibilityTracker;
import org.objectweb.proactive.multiactivity.execution.RequestExecutor;
import org.objectweb.proactive.multiactivity.limits.ThreadManager;
import org.objectweb.proactive.multiactivity.limits.ThreadTracker;
import org.objectweb.proactive.multiactivity.priority.PriorityManager;
import org.objectweb.proactive.multiactivity.priority.PriorityTracker;
import org.objectweb.proactive.multiactivity.policy.DefaultServingPolicy;
import org.objectweb.proactive.multiactivity.policy.ServingPolicy;


/**
 * This class extends the {@link Service} class and adds the capability of serving more methods in parallel. 
 * <br> The decision of which methods can run in parallel is made based on annotations set by the user. 
 * These annotations are to be found in the <i> org.objectweb.proactive.annotation.multiactivity</i> package.
 * 
 * @author  The ProActive Team
 */
public class MultiActiveService extends Service {

    public static final boolean LIMIT_ALL_THREADS = true;
    public static final boolean LIMIT_ACTIVE_THREADS = false;
    public static final boolean REENTRANT_SAME_THREAD = true;
    public static final boolean REENTRANT_SEPARATE_THREAD = false;

    public int activeServes = 0;
    public LinkedList<Integer> serveHistory = new LinkedList<Integer>();
    public LinkedList<Integer> serveTsts = new LinkedList<Integer>();
    
    private boolean isConfiguredThroughAnnot = false;
    private int totalReservedThreads;
    
    RequestExecutor executor;
    

    /**
     * MultiActiveService that will be able to optionally use a policy, and will deploy each serving request on a 
     * separate physical thread.
     * 
     * @param body The body of the active object.
     */
    public MultiActiveService(Body body) {
        super(body);

        //not doing the initialization here because after creating the request executor
        //we are not compatible with 'fifoServing' any more.

    }
    
    private void init() {
        if (executor != null)
            return;

        AnnotationProcessor annotationProcessor = new AnnotationProcessor(body.getReifiedObject().getClass());

        // Setting the compatibility manager from what was extracted from annotations
        CompatibilityManager compatibilityManager = new CompatibilityTracker(
        		requestQueue, annotationProcessor.getCompatibilityMap());
        
        // Filling priority structures according to what was extracted from annotations
        PriorityManager priorityManager = new PriorityTracker(
        		compatibilityManager, annotationProcessor.getPriorityMap());
        
        // Setting thread configuration from what was extracted from annotations
        isConfiguredThroughAnnot = annotationProcessor.getThreadMap().isConfiguredThroughAnnot();
        totalReservedThreads = annotationProcessor.getThreadMap().getTotalConfiguredThread();
        ThreadManager threadManager = new ThreadTracker(
        		compatibilityManager, annotationProcessor.getThreadMap());
        
        // Building executor and configuring it with all required information for scheduling
        executor = new RequestExecutor(body, compatibilityManager, priorityManager, threadManager);
        executor.configure(threadManager.getThreadPoolSize(), 
        		threadManager.getHardLimit(), threadManager.getHostReentrant());
    }

    /**
     * Service that relies on the default parallel policy to extract requests from the queue.
     * @param maxActiveThreads maximum number of allowed threads inside the multi-active object
     * @param hardLimit false if the above limit is applicable only to active (running) threads, but not the waiting ones
     * @param hostReentrant true if re-entrant calls should be hosted on the issuer's thread
     */
    public void multiActiveServing(int maxActiveThreads, boolean hardLimit, boolean hostReentrant) {
        init();

        if (!isConfiguredThroughAnnot) {
        	executor.configure(maxActiveThreads <= totalReservedThreads ? totalReservedThreads + 
        			ThreadManager.THREAD_POOL_MARGIN : maxActiveThreads, hardLimit, hostReentrant);
        }
        executor.execute(new DefaultServingPolicy());
    }

    /**
     * Service that relies on the default parallel policy to extract requests from the queue. Threads are soft-limited and re-entrance on the same thread is disabled.
     * @param maxActiveThreads maximum number of allowed threads inside the multi-active object
     */
    public void multiActiveServing(int maxActiveThreads) {
        init();
        if (!isConfiguredThroughAnnot) {
        	executor.configure(maxActiveThreads <= totalReservedThreads ? totalReservedThreads + 
        			ThreadManager.THREAD_POOL_MARGIN : maxActiveThreads, false, false);
        }
        executor.execute(new DefaultServingPolicy());
    }

    /**
     * Service that relies on the default parallel policy to extract requests from the queue. Threads are not limited and re-entrance on the same thread is disabled.
     */
    public void multiActiveServing() {
        init();
        if (!isConfiguredThroughAnnot) {
        	executor.configure(Integer.MAX_VALUE, false, false);
        }
        executor.execute(new DefaultServingPolicy());
    }

    /**
     * Creates the serving policy to use to schedule requests.
     * 
     * @return The serving policy to use to schedule requests.
     */
    protected ServingPolicy createServingPolicy() {
        return new DefaultServingPolicy();
    }

    /**
     * Service that relies on a user-defined policy to extract requests from the queue.
     * @param policy
     * @param priorityConstraints priority constraints to apply
     * @param maxActiveThreads maximum number of allowed threads inside the multi-active object
     * @param hardLimit false if the above limit is applicable only to active (running) threads, but not the waiting ones
     * @param hostReentrant true if re-entrant calls should be hosted on the issuer's thread
     */
    public void policyServing(ServingPolicy policy, int maxActiveThreads, boolean hardLimit,
            boolean hostReentrant) {
        init();
        if (!isConfiguredThroughAnnot) {
        	executor.configure(maxActiveThreads <= totalReservedThreads ? totalReservedThreads + 
        			ThreadManager.THREAD_POOL_MARGIN : maxActiveThreads, hardLimit, hostReentrant);
        }
        executor.execute(policy);
    }

    /**
     * Service that relies on a user-defined policy to extract requests from the queue. Threads are soft-limited and re-entrance on the same thread is disabled.
     * @param maxActiveThreads maximum number of allowed threads inside the multi-active object
     */
    public void policyServing(ServingPolicy policy, int maxActiveThreads) {
        init();
        if (!isConfiguredThroughAnnot) {
        	executor.configure(maxActiveThreads <= totalReservedThreads ? totalReservedThreads + 
        			ThreadManager.THREAD_POOL_MARGIN : maxActiveThreads, false, false);
        }
        executor.execute(policy);
    }

    /**
     * Service that relies on a user-defined policy to extract requests from the queue. Threads are not limited and re-entrance on the same thread is disabled.
     * @param policy Serving policy to use
     */
    public void policyServing(ServingPolicy policy) {
        init();
        if (!isConfiguredThroughAnnot) {
        	executor.configure(Integer.MAX_VALUE, false, false);
        }
        executor.execute(policy);
    }

    /**
     * Returns the object through which the service's properties can be modified at run-time.
     * @return serving controller
     */
    public ServingController getServingController() {
        //this init runs only once even if invoked many times
        init();

        return executor;
    }
    
    /**
     * Returns the list of requests that have been served by this multiactive 
     * service (from oldest to latest).
     * @return Served requests
     */
    public LinkedList<Request> getServingHistory() {
    	return this.executor.getServingHistory();
    }

    public RequestExecutor getRequestExecutor() {
        return this.executor;
    }

}
