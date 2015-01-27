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
package org.objectweb.proactive.extra.multiactivecan.test;

import java.util.LinkedList;
import java.util.List;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.annotation.multiactivity.MemberOf;
import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;
import org.objectweb.proactive.extra.multiactivecan.Key;
import org.objectweb.proactive.extra.multiactivecan.Peer;


public class LookupPeer extends Peer implements InitActive {

    public LookupPeer() {
    }

    public LookupPeer(String name, boolean isMao) {
        super(name, isMao);
    }

    @MemberOf("lookup")
    public BooleanWrapper lookupAllKeys(int canSize, int totalMessages) {
        List<Key> keys = LookupTest.generateKeys(canSize);

        List<Object> results = new LinkedList<Object>();

        for (int i = 0; i < totalMessages / keys.size(); i++) {
            for (Key k : keys) {
                results.add(lookup(k));
            }
        }

        PAFuture.waitForAll(results);

        return new BooleanWrapper(true);
    }

    @Override
    public void initActivity(Body body) {
        if (!IS_MAO) {
            body.setImmediateService("lookupAllKeys", false);
        }
    }

}
