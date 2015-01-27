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

import java.util.HashMap;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.core.UniqueID;


/**
 * Static class that pairs {@link Body} instances with {@link FutureWaiter}s. Since each body has only one associated service, and each service
 * can have only one internal future waiter, the mapping is one-to-one.
 * @author The ProActive Team
 *
 */
public class FutureWaiterRegistry {
    private static HashMap<UniqueID, FutureWaiter> registry = new HashMap<UniqueID, FutureWaiter>();

    /**
     * Pairs a future waiter with a body.
     * @param id the ID of the body
     * @param fl the future waiter implementation
     */
    public static void putForBody(UniqueID id, FutureWaiter fl) {
        registry.put(id, fl);
    }

    /**
     * Returns the future waiter associated with a body. 
     * @param id the ID of the body
     * @return the future waiter instance, or null if there is no binding
     */
    public static FutureWaiter getForBody(UniqueID id) {
        return registry.get(id);
    }

}
