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
package functionalTests.multiactivities.patterns;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.annotation.multiactivity.DefineGroups;
import org.objectweb.proactive.annotation.multiactivity.Group;
import org.objectweb.proactive.annotation.multiactivity.MemberOf;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.multiactivity.MultiActiveService;


@DefineGroups( { @Group(name = "iteration", selfCompatible = true, parameter = "java.lang.Integer", condition = "equals") })
public class ParallelFor implements RunActive {
    private static int MAX_THREADS;
    private static int OUTER_LOOP;
    AtomicInteger cnt;
    ParallelFor me;

    public ParallelFor() {
        /*cnt = new AtomicInteger();
        cnt.set(0);*/
    }

    private void parallelFor(int from, int to) {
        System.out.println("Starting...");
        Date t1 = new Date();
        for (int i = from; i < to; i++) {
            me.forIteration(i);
        }
        me.forBarrier();
        Date t2 = new Date();
        System.out.println("total time=" + (t2.getTime() - t1.getTime()));
    }

    @MemberOf("iteration")
    public void forIteration(Integer i) {
        //cnt.incrementAndGet();
        //do something
        //Date t1 = new Date();
        try {
            Thread.sleep(OUTER_LOOP);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        /*  ArrayList<Integer> list = new ArrayList<Integer>();
          
          for (int x=0; x<OUTER_LOOP; x++) {
              list.add(x);
              for (int y=0; y<x; y++) {
                  list.set(y, list.get(y)+list.get(x));
              }
          }*/

        //Date t2 = new Date();
        //System.out.println(i+" time="+(t2.getTime()-t1.getTime()));
        //cnt.decrementAndGet();
    }

    public boolean forBarrier() {
        return true;
    }

    public static ParallelFor getInstance() throws ActiveObjectCreationException, NodeException {
        ParallelFor pf = PAActiveObject.newActive(ParallelFor.class, null);
        pf.me = pf;
        return pf;
    }

    public static void main(String[] args) throws ActiveObjectCreationException, NodeException {
        MAX_THREADS = Integer.parseInt(args[1]);
        OUTER_LOOP = Integer.parseInt(args[2]);
        ParallelFor pf = getInstance();
        pf.parallelFor(0, Integer.parseInt(args[0]));
        System.exit(0);
    }

    @Override
    public void runActivity(Body body) {
        new MultiActiveService(body).multiActiveServing(MAX_THREADS, true, false);

    }

}
