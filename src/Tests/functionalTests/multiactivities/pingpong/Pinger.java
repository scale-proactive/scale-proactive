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

import org.objectweb.proactive.Body;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.Service;
import org.objectweb.proactive.annotation.multiactivity.Compatible;
import org.objectweb.proactive.annotation.multiactivity.DefineGroups;
import org.objectweb.proactive.annotation.multiactivity.DefineRules;
import org.objectweb.proactive.annotation.multiactivity.Group;
import org.objectweb.proactive.annotation.multiactivity.MemberOf;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.multiactivity.MultiActiveService;


/**
 * Class to test ping-pong-like interactions
 * @author The ProActive Team
 *
 */
@DefineGroups( { @Group(name = "gPing", selfCompatible = true),
        @Group(name = "gPong", selfCompatible = true), @Group(name = "gStarter", selfCompatible = false) })
@DefineRules( { @Compatible( { "gStarter", "gPing" }), @Compatible( { "gStarter", "gPong" }) })
public class Pinger implements RunActive {
    private Integer count = 3;
    private Pinger other;
    private Boolean multiActive;

    public Pinger() {
        // for PA
    }

    public Pinger(Boolean multiActive) {
        this.other = other;
        this.multiActive = multiActive;
    }

    public void setOther(Pinger other) {
        this.other = other;
    }

    /**
     * Set the pair of this object
     * @return
     */
    public Pinger getOther() {
        return this.other;
    }

    /**
     * Call the pair's ping method
     * @return
     */
    @MemberOf("gPong")
    /*@Reads("pong")*/
    public IntWrapper pong() {
        count--;
        System.out.println("* Pong" + count + "!");
        if (count > 0) {
            other.ping().getIntValue();
        } else {
            System.out.println("* Done in pong");
        }
        return new IntWrapper(count);
    }

    /**
     * Call the pair's pong method
     * @return
     */
    @MemberOf("gPing")
    /*@Reads("ping")*/
    public IntWrapper ping() {
        count--;
        System.out.println("* Ping" + count + "!");
        if (count > 0) {
            other.pong().getIntValue();
        } else {
            System.out.println("* Done in ping");
        }
        return new IntWrapper(count);
    }

    /**
     * Method to start off -- it will call the pair's ping method 
     * @return
     */
    @MemberOf("gStarter")
    /*@Modifies("count")
    @Reads({"ping","pong"})*/
    public IntWrapper startWithPing() {
        return new IntWrapper(other.ping().getIntValue());
    }

    /**
     * Method to start off -- that will deadblock
     * @return
     */
    public IntWrapper startWithPong() {
        return new IntWrapper(other.pong().getIntValue());
    }

    @Override
    public void runActivity(Body body) {
        if (this.multiActive) {
            (new MultiActiveService(body)).multiActiveServing(1, false, false);
        } else {
            (new Service(body)).fifoServing();
        }

    }
}
