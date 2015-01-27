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

import java.util.Date;

import org.apache.log4j.Logger;


/**
 * Use this to start an SCC search.
 * @author The ProActive Team
 *
 */
public class GraphMaster {

    public static void main(String[] args) {
        Logger logger = Logger.getLogger("SCC");

        if (args.length < 4) {
            printUsage();
            return;
        }

        int workerCount = Integer.parseInt(args[0]);

        Deployer deployer = new Deployer();
        GraphWorker[] workers;
        if (args.length == 5) {
            workers = deployer.createAndDeploy(workerCount, args[4].split(";"), Integer.parseInt(args[2]),
                    args[3].startsWith("true"));
        } else {
            workers = deployer.createAndDeploy(workerCount, Integer.parseInt(args[2]), args[3]
                    .startsWith("true"));
        }
        if (workers == null) {
            logger.error("Failed to create workers!");
            return;
        }
        logger.info("Created " + workerCount + " workers.");

        /*DataManager data = new DataManager(workers);
        data.loadAndDistribute(args[1]);
        logger.info("Loaded graph from "+args[1]);*/
        Date before = new Date();
        int x = 0;
        for (GraphWorker gw : workers) {
            gw.loadEdges(workers, args[1], x);
            x++;
        }

        int numNodes = 0;
        for (GraphWorker gw : workers) {
            numNodes += gw.getOwnedNodesCount();
        }
        System.out.println(new Date().getTime() - before.getTime());
        Executer executer = new Executer(workers);
        logger.info("Starting executer.");
        //-1 == infinite branches
        executer.runAlgorithm(-1, numNodes);

        logger.info("Killing all workers and exiting.");
        deployer.killAll();
        System.exit(0);
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out
                .println("<Number of workers> <Path to graph> <nb. of threads (0 for legacy mode)> <hard limit flag>");
    }

}
