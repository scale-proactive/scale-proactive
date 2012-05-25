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

import org.objectweb.proactive.core.body.future.Future;


/**
 * Interface for classes to whom a future proxy can delegate the task of waiting for a future value.
 * The proxy has to announce ({@link #futureArrived(Future)}) the arrival of the value to the waiter, thus this can return from the {@link #waitForFuture(Future)}
 * call.
 * @author The ProActive Team
 *
 */
public interface FutureWaiter {

    /**
     * Can be used to replace the waiting inside a futre's proxy. This method should return only when the future's value has arrived. This is signaled
     * to the waiter by the proxy with the {@link #futureArrived(Future)} method.
     * @param future The future upon which the wait is performed.
     */
    public void waitForFuture(Future future);

    /**
     * Can be used to 'notify' the future waiter about the arrival of an awaited future from the future's proxy. 
     * All {@link #waitForFuture(Future)}s on the same future should return as a consequence.
     * @param future The future whose value has arrived.
     */
    public void futureArrived(Future future);

}
