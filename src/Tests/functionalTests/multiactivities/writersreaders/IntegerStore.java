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

import org.objectweb.proactive.Body;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.annotation.multiactivity.Modifies;
import org.objectweb.proactive.annotation.multiactivity.Reads;
import org.objectweb.proactive.multiactivity.MultiActiveService;
import org.objectweb.proactive.multiactivity.policy.ServingPolicy;
import org.objectweb.proactive.multiactivity.policy.ServingPolicyFactory;


/**
 * This class simply wraps around an integer value and exposes a few methods for interacting with
 * this value.
 * @author The ProActive Team
 *
 */
public class IntegerStore implements RunActive {

    private Integer value = new Integer(0);
    private Boolean greedy;
    public int READ_TIME = 100;

    public IntegerStore() {

    }

    public IntegerStore(Boolean greedy) {
        this.greedy = greedy;
    }

    /**
     * There are two serving strategies for this body: simple multiactive and 
     * greedy multiactive (that does not preserve the order of requests)
     */
    @Override
    public void runActivity(Body body) {
        if (greedy) {
            ServingPolicy greedy = ServingPolicyFactory.getGreedyMultiActivityPolicy();
            (new MultiActiveService(body)).policyServing(greedy);
        } else {
            (new MultiActiveService(body)).multiActiveServing();
        }

    }

    /**
     * This method writes the value, so it is not compatible with anyone else.
     * The writing operation will take 1s.
     * @param newInt
     * @return
     */
    @Modifies( { "value" })
    public Integer write(Integer newInt) {
        System.out.print("(Started serving write) ");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        value = newInt;
        return newInt;
    }

    /**
     * Sets the time needed to read the internal value.
     * Changing it can help simulating several scenarios.
     * @param millisecond
     */
    public void setReadTime(int millisecond) {
        READ_TIME = millisecond;
    }

    /**
     * This method reads the value of the integer, and since it does not
     * change anything, instances of this method can run in parallel.
     * They will take READ_TIME milliseconds to run (default = 100)
     * @return
     */
    @Reads( { "value" })
    public Integer read() {
        System.out.print("(Started serving read) ");
        try {
            Thread.sleep(READ_TIME);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return value;
    }

    /**
     * This method will call an other IntegerStore's read() method. In case we feed the IntegerStore itself
     * here, we will have a deadlock.
     * @param ist
     * @return
     */
    public Integer instantLock(IntegerStore ist) {
        System.out.println("Now I will call an imcompatilbe method on myself (indirectly). Should deadlock.");
        return ist.read();
    }

}
