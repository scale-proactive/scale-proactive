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
package org.objectweb.proactive.extra.multiactivecan;

import java.io.Serializable;

import org.objectweb.proactive.annotation.multiactivity.Compatible;
import org.objectweb.proactive.annotation.multiactivity.DefineGroups;
import org.objectweb.proactive.annotation.multiactivity.DefineRules;
import org.objectweb.proactive.annotation.multiactivity.Group;
import org.objectweb.proactive.annotation.multiactivity.MemberOf;


@DefineGroups( {
        @Group(name = "add_param", selfCompatible = true, parameter = "proactive.multiactivity.can.Key"),
        @Group(name = "join_from", selfCompatible = false) })
@DefineRules( { @Compatible(value = { "add_param", "join_from" }, condition = "!this.dueToChange"),
        @Compatible( { "add_param", "lookup" }) })
public class ParallelPeer extends Peer {

    public ParallelPeer() {
        super();
    }

    public ParallelPeer(String name, boolean mao) {
        super(name, mao);
    }

    public ParallelPeer(String name) {
        super(name);
    }

    private boolean dueToChange(Key k) {
        return router.isRecentLocal(k);
    }

    @Override
    @MemberOf("join_from")
    public JoinResponse joinFrom(Peer other) {
        // TODO Auto-generated method stub
        return super.joinFrom(other);
    }

    @Override
    @MemberOf("add_param")
    public void add(Key k, Serializable value) {
        // TODO Auto-generated method stub
        super.add(k, value);
    }

    @Override
    @MemberOf("add_param")
    public Serializable lookup(Key k) {
        // TODO Auto-generated method stub
        return super.lookup(k);
    }

}
