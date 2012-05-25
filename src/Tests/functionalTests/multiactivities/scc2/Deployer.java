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
package functionalTests.multiactivities.scc2;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;


/**
 * Create a given number of workers and place them on a given set of nodes, or locally.
 * @author The ProActive Team
 *
 */
public class Deployer {

    private GraphWorker[] workers;

    public GraphWorker[] createAndDeploy(int cnt, String[] hosts, int threads, boolean hardLimited) {
        workers = new GraphWorker[cnt];
        boolean ok = true;

        for (int i = 0; i < cnt; i++) {
            Object[] params = new Object[3];
            params[0] = "Node" + i + "";
            params[1] = threads;
            params[2] = hardLimited;
            try {
                if (hosts.length > 0) {

                    workers[i] = PAActiveObject.newActive(GraphWorker.class, params, hosts[i % hosts.length]);
                } else {
                    workers[i] = PAActiveObject.newActive(GraphWorker.class, params);
                }
                workers[i].init();
            } catch (ActiveObjectCreationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                ok = false;
            } catch (NodeException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                ok = false;
            }
        }

        return ok ? workers : null;
    }

    public GraphWorker[] createAndDeploy(int cnt, int threads, boolean hardLimited) {
        return createAndDeploy(cnt, new String[0], threads, hardLimited);
    }

    public GraphWorker[] getWorkers() {
        return workers;
    }

    public void kill(GraphWorker[] w) {
        for (GraphWorker gw : w) {
            int max = 0;
            for (Integer i : gw.getActiveServeCount()) {
                if (i > max) {
                    max = i;
                }
            }
            System.out.println("Max number of threads = " + max);
            PAActiveObject.terminateActiveObject(gw, true);
        }
    }

    public void killAll() {
        kill(workers);
    }

}
