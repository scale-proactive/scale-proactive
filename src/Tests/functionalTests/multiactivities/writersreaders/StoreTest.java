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
package functionalTests.multiactivities.writersreaders;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;


public class StoreTest {

    /**
     * You should see requests executed in order, with reads executed in parallel.
     * @throws ActiveObjectCreationException
     * @throws NodeException
     */
    @Test
    public void simpleMultiActiveTest() throws ActiveObjectCreationException, NodeException {
        Object[] constrPrm = { false };
        IntegerStore ist = PAActiveObject.newActive(IntegerStore.class, constrPrm);
        System.out.println("\nNORMAL TEST:");
        createAndRunThreads(ist);
    }

    /**
     * This test will block while serving the first read request, as it will take infinity time to finish,
     * and it is impossible to schedule anything besides.
     * @throws ActiveObjectCreationException
     * @throws NodeException
     */
    @Test(timeout = 8000)
    public void blockingSimpleMultiActiveTest() throws ActiveObjectCreationException, NodeException {
        Object[] constrPrm = { false };
        IntegerStore ist = PAActiveObject.newActive(IntegerStore.class, constrPrm);
        //setting read delay to "infinity"
        ist.setReadTime(100000);
        System.out.println("\nBLOCKING TEST:");
        createAndRunThreads(ist);
    }

    /**
     * This test will execute the first write, than *all* reads from the queue, than writes one by one.
     * @throws ActiveObjectCreationException
     * @throws NodeException
     */
    @Test
    public void greedyMultiActiveTest() throws ActiveObjectCreationException, NodeException {
        Object[] constrPrm = { true };
        IntegerStore ist = PAActiveObject.newActive(IntegerStore.class, constrPrm);
        System.out.println("\nGREEDY TEST: ");
        createAndRunThreads(ist);
    }

    /**
     * This test will execute the first write, than *all* reads from the queue -- however, since these take infinity
     * to finish, the scheduler will not do anything after.
     * @throws ActiveObjectCreationException
     * @throws NodeException
     */
    @Test(timeout = 8000)
    public void blockingGreedyMultiActiveTest() throws ActiveObjectCreationException, NodeException {
        Object[] constrPrm = { true };
        IntegerStore ist = PAActiveObject.newActive(IntegerStore.class, constrPrm);
        //setting read delay to "infinity"
        ist.setReadTime(100000);
        System.out.println("\nBLOCKING GREEDY TEST: ");
        createAndRunThreads(ist);
    }

    /**
     * This test checks if the MAO can deadlock itself because of method incompatibilities
     * @throws ActiveObjectCreationException
     * @throws NodeException
     */
    @Test(timeout = 8000)
    public void deadblockinMethodTest() throws ActiveObjectCreationException, NodeException {
        Object[] constrPrm = { true };
        IntegerStore ist = PAActiveObject.newActive(IntegerStore.class, constrPrm);
        System.out.println("\nDEADBLOCKING METHOD TEST: ");
        System.out.println("Value is: " + ist.instantLock(ist));
    }

    private void createAndRunThreads(IntegerStore ist) {
        //the requests will be in this order (W=write, R=read)
        // W R W R R R R W

        List<Runnable> tasks = new LinkedList<Runnable>();
        tasks.add(new Writer(ist));
        tasks.add(new Reader(ist));
        tasks.add(new Writer(ist));
        tasks.add(new Reader(ist));
        tasks.add(new Reader(ist));
        tasks.add(new Reader(ist));
        tasks.add(new Reader(ist));
        tasks.add(new Writer(ist));

        for (int i = 0; i < tasks.size() - 1; i++) {
            (new Thread(tasks.get(i))).start();
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        (tasks.get(tasks.size() - 1)).run();
    }

    public class Writer implements Runnable {
        public IntegerStore is;

        public Writer(IntegerStore is) {
            this.is = is;
        }

        @Override
        public void run() {
            System.out.println("Written " + is.write(new Integer((int) (Math.random() * 100))));
        }

    }

    public class Reader implements Runnable {
        public IntegerStore is;

        public Reader(IntegerStore is) {
            this.is = is;
        }

        @Override
        public void run() {
            System.out.println("Read " + is.read());
        }

    }

}
