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
package functionalTests.multiactivities.pingpong;

import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;


public class PingPongTest {

    /**
     * This one will timeout, becaue the reentrant code is not working in standard PA
     * @throws ActiveObjectCreationException
     * @throws NodeException
     */
    @Test(timeout = 8000)
    public void testNoMultiActive() throws ActiveObjectCreationException, NodeException {
        Object[] constrPrm = { false };
        Pinger a = PAActiveObject.newActive(Pinger.class, constrPrm);
        Pinger b = PAActiveObject.newActive(Pinger.class, constrPrm);
        a.setOther(b);
        b.setOther(a);

        System.out.println();
        System.out.println("Test: should see one ping");
        System.out.println(a.startWithPing());
    }

    /**
     * This will work fine, because we have multi-activity :D
     * @throws ActiveObjectCreationException
     * @throws NodeException
     */
    @Test
    public void testMultiActive() throws ActiveObjectCreationException, NodeException {
        Object[] constrPrm = { true };
        Pinger a = PAActiveObject.newActive(Pinger.class, constrPrm);
        Pinger b = PAActiveObject.newActive(Pinger.class, constrPrm);
        a.setOther(b);
        b.setOther(a);

        System.out.println();
        System.out.println("Test: should see ping-pongs");
        System.out.println(a.ping());
    }

    /**
     * This one will also freeze up because the startWithPong() method is not annotated to run with someone else...
     * @throws ActiveObjectCreationException
     * @throws NodeException
     */
    @Test
    //(timeout=8000)
    public void testMultiActiveBadAnnotation() throws ActiveObjectCreationException, NodeException {
        Object[] constrPrm = { true };
        Pinger a = PAActiveObject.newActive(Pinger.class, constrPrm);
        Pinger b = PAActiveObject.newActive(Pinger.class, constrPrm);
        a.setOther(b);
        b.setOther(a);

        System.out.println();
        System.out.println("Test: should see one pong");
        System.out.println(a.startWithPong());
    }

}
