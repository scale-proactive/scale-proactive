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
package org.objectweb.proactive.multiactivity.execution;

import org.objectweb.proactive.core.body.future.FutureID;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.body.tags.Tag;
import org.objectweb.proactive.core.body.tags.tag.DsiTag;
import org.objectweb.proactive.multiactivity.priority.PriorityConstraint;


/**
 * Wrapper class for a request. Apart from the actual serving it also performs
 * calls for thread registering and unregistering inside the executor and
 * callback after termination in the wrapping service.
 * 
 * @author The ProActive Team
 */
public class RunnableRequest implements Runnable {

    private final RequestExecutor requestExecutor;

    private final Request request;

    private boolean canRun = true;

    private FutureID waitingOn;

    private RunnableRequest hostedOn;

    private String sessionTag;

    // the priority constraint associated to this runnable request
    // not so nice to have this field here but it avoids additional maps
    private PriorityConstraint priorityConstraint;

    private boolean boosted = false;

    public RunnableRequest(RequestExecutor requestExecutor, Request r) {
        this.requestExecutor = requestExecutor;
        this.request = r;
    }

    public Request getRequest() {
        return this.request;
    }

    @Override
    public void run() {
        this.requestExecutor.serve(this);
    }

    @Override
    public String toString() {
        return "Wrapper for " + request.toString();
    }

    /**
     * Tell this request that is can carry on executing, because the event it
     * has been waiting for has arrived.
     * 
     * @param canRun
     */
    public void setCanRun(boolean canRun) {
        this.canRun = canRun;
    }

    /**
     * Check whether the inner request can carry on the execution.
     * 
     * @return true if the inner request can carry on the execution.
     */
    public boolean canRun() {
        return canRun;
    }

    /**
     * Tell the outer world on what future's value is this request performing a
     * wait-by-necessity.
     * 
     * @param waitingOn
     */
    public void setWaitingOn(FutureID waitingOn) {
        this.waitingOn = waitingOn;
    }

    /**
     * Find out on what future's value is this request performing a
     * wait-by-necessity.
     * 
     * @return on what future's value is this request performing a
     *         wait-by-necessity.
     */
    public FutureID getWaitingOn() {
        return waitingOn;
    }

    private void setSessionTag(String sessionTag) {
        this.sessionTag = sessionTag;
    }

    public String getSessionTag() {
        if (sessionTag != null) {
            return sessionTag;
        } else {
            Tag tag = getRequest().getTags().getTag(DsiTag.IDENTIFIER);
            if (tag != null) {
                Object tagObject = tag.getData();
                if (tagObject != null) {
                    String[] tagData = ((String) (tagObject)).split("::");
                    setSessionTag(tagData[0] + "::" + tagData[1]);
                }
            }
            return sessionTag;
        }
    }

    public void setHostedOn(RunnableRequest hostedOn) {
        this.hostedOn = hostedOn;
    }

    public RunnableRequest getHostedOn() {
        return hostedOn;
    }

    public boolean isBoosted() {
        return this.boosted;
    }

    public void setBoosted() {
        this.boosted = true;
    }

    public PriorityConstraint getPriorityConstraint() {
        return this.priorityConstraint;
    }

    public void setPriorityConstraint(PriorityConstraint priorityConstraint) {
        this.priorityConstraint = priorityConstraint;
    }

}
