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
package functionalTests.multiactivities.microbenchmark;

import java.io.IOException;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.annotation.multiactivity.DefineGroups;
import org.objectweb.proactive.annotation.multiactivity.Group;
import org.objectweb.proactive.annotation.multiactivity.MemberOf;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;
import org.objectweb.proactive.multiactivity.MultiActiveService;


@DefineGroups( { @Group(name = "default", selfCompatible = true) })
public class Circle implements RunActive {

    public static void main(String[] args) throws IOException, ActiveObjectCreationException, NodeException {
        int NUM = 100;

        Circle[] workers = new Circle[NUM];
        for (int i = 0; i < NUM; i++) {
            workers[i] = PAActiveObject.newActive(Circle.class, null);
        }
        workers[0].setNeighbour(workers[NUM - 1]);
        for (int i = 1; i < NUM; i++) {
            workers[i].setNeighbour(workers[(i - 1)]);
        }

        workers[0].passFirstMessage();
    }

    public Circle() {
        // TODO Auto-generated constructor stub
    }

    private long startTime = 0;
    private int LIMIT = 100;
    private int messageCount = 0;
    private Circle neighbour;

    public void setNeighbour(Circle cw) {
        this.neighbour = cw;
    }

    @MemberOf("default")
    public void passFirstMessage() {
        startTime = System.currentTimeMillis();
        passMessage();
    }

    @MemberOf("default")
    public BooleanWrapper passMessage() {
        //System.out.println(this.toString());
        messageCount++;
        if (messageCount < LIMIT) {
            BooleanWrapper bw = neighbour.passMessage();
            /*if (!bw.getBooleanValue()){
                System.out.println("XXX");
            }*/
            /*while(true) {
               try {
                  Thread.sleep(10);
               } catch (Exception e) {
                  //
               }
            }*/
        } else {
            System.out.println(System.currentTimeMillis() - startTime);
        }

        return new BooleanWrapper(true);
    }

    @Override
    public void runActivity(Body body) {
        //new Service(body).fifoServing();
        new MultiActiveService(body).multiActiveServing(1, false, false);

    }

}
