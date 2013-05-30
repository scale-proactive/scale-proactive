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

import org.objectweb.proactive.Body;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.annotation.multiactivity.Compatible;
import org.objectweb.proactive.annotation.multiactivity.DefineGroups;
import org.objectweb.proactive.annotation.multiactivity.DefineRules;
import org.objectweb.proactive.annotation.multiactivity.Group;
import org.objectweb.proactive.annotation.multiactivity.MemberOf;
import org.objectweb.proactive.multiactivity.MultiActiveService;
import org.objectweb.proactive.multiactivity.policy.ServingPolicyFactory;


@DefineGroups( { @Group(name = "gCounter", selfCompatible = false),
        @Group(name = "gPoller", selfCompatible = true),
        @Group(name = "gPoller_Bogus", selfCompatible = true) })
@DefineRules( { @Compatible(value = { "gCounter", "gPoller" }),
        @Compatible(value = { "gCounter", "gPoller_Bogus" }) })
public class InfiniteCounter implements RunActive {
    private Long value = new Long(0);
    private Boolean multiActive;

    public InfiniteCounter() {

    }

    public InfiniteCounter(Boolean multiActive) {
        this.multiActive = multiActive;
    }

    @Override
    public void runActivity(Body body) {
        if (multiActive) {
            (new MultiActiveService(body)).multiActiveServing(2, false, true);
        } else {
            (new MultiActiveService(body)).policyServing(ServingPolicyFactory.getSingleActivityPolicy());
        }

    }

    @MemberOf("gCounter")
    public void countToInfinity() {
        System.out.println("Counting to infinity!");
        while (value != null) {
            synchronized (value) {
                value++;
            }
        }
    }

    @MemberOf("gPoller")
    public Long noReturnPollValue() {
        while (value != -1) {
            // ...
        }
        return value;
    }

    @MemberOf("gPoller")
    public Long pollValue() {
        synchronized (value) {
            System.out.println("Polling value...");
            return value.longValue();
        }
    }

}
