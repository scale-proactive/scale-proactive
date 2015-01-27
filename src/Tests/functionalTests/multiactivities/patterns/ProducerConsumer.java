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
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.annotation.multiactivity.DefineGroups;
import org.objectweb.proactive.annotation.multiactivity.Group;
import org.objectweb.proactive.annotation.multiactivity.MemberOf;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.multiactivity.MultiActiveService;
import org.objectweb.proactive.multiactivity.ServingController;


@DefineGroups( { @Group(name = "queue", selfCompatible = true) })
public class ProducerConsumer implements RunActive {
    Queue<Integer> queue = new LinkedBlockingQueue<Integer>();
    ServingController control;

    @MemberOf("queue")
    public void produce() {
        boolean go = true;
        int x = 0;
        while (go) {
            queue.add((int) Math.random());
            x++;
            if (x == 20000) {
                control.incrementNumberOfConcurrent(20000);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                x = 0;
            }
        }
    }

    @MemberOf("queue")
    public int consume() {
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        control.decrementNumberOfConcurrent();
        return queue.remove();
    }

    @Override
    public void runActivity(Body body) {
        MultiActiveService mas = new MultiActiveService(body);
        control = mas.getServingController();
        mas.multiActiveServing(1);
    }

    public static void main(String[] args) throws ActiveObjectCreationException, NodeException {
        ProducerConsumer pc = PAActiveObject.newActive(ProducerConsumer.class, null);
        pc.produce();
        boolean ok = true;
        Date s = new Date();
        int cnt = 0;
        int bigCnt = 0;
        while (ok) {
            pc.consume();
            cnt++;
            if (cnt == 10) {
                bigCnt++;
                cnt = 0;
            }

            if (bigCnt % 10 == 0 && cnt == 0) {
                System.out.println("Avg. Speed = " +
                    ((bigCnt * 10.0) / (((new Date().getTime() - s.getTime())) / 1000.0)) + " read/s");
                //s = new Date();
            }
        }
    }

}
