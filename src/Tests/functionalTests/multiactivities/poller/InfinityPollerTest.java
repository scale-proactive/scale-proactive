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
package functionalTests.multiactivities.poller;

import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;


public class InfinityPollerTest {

    @Test(timeout = 8000)
    public void noMultiActiveTest() throws ActiveObjectCreationException, NodeException, InterruptedException {
        Object[] constrPrm = { false };
        InfiniteCounter cti = PAActiveObject.newActive(InfiniteCounter.class, constrPrm);
        System.out.println("TEST: will fail to poll");
        cti.countToInfinity();

        for (int i = 0; i < 4; i++) {
            System.out.println(cti.pollValue());
            Thread.sleep(500);
        }
    }

    @Test(timeout = 8000)
    public void multiActiveTest() throws ActiveObjectCreationException, NodeException, InterruptedException {
        Object[] constrPrm = { true };
        InfiniteCounter cti = PAActiveObject.newActive(InfiniteCounter.class, constrPrm);

        System.out.println("TEST: will poll values successfully");
        cti.countToInfinity();

        for (int i = 0; i < 4; i++) {
            System.out.println("I've peeked at the value and it is: " + cti.pollValue());
            Thread.sleep(500);
        }
    }

    @Test(timeout = 8000)
    public void blockingMultiActiveTest() throws ActiveObjectCreationException, NodeException,
            InterruptedException {
        Object[] constrPrm = { true };
        InfiniteCounter cti = PAActiveObject.newActive(InfiniteCounter.class, constrPrm);

        System.out.println("TEST: will block, because of bads annotations");
        cti.countToInfinity();

        System.out.println("Infinite method which does not let others poll the value: " +
            cti.noReturnPollValue());
        for (int i = 0; i < 4; i++) {
            System.out.println(cti.pollValue());
        }
    }

}
