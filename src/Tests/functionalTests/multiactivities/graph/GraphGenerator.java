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
package functionalTests.multiactivities.graph;

public class GraphGenerator {

    public static void getUniformDirectedGraph(int vertices, float density) {

        for (int i = 0; i < vertices; i++) {
            for (int j = 0; j < vertices; j++) {

                double chance = Math.random();
                if (chance > 1 - density) {
                    System.out.println(i + "-" + j);
                }

            }
        }
    }

    public static void getPartitionedDirectedGraph(int vertices, float density, int partition) {

        for (int i = 0; i < vertices; i++) {
            for (int j = 0; j < vertices; j++) {

                double chance = Math.random();
                if (chance > 1 - density && i % partition == j % partition) {
                    System.out.println(i + "-" + j);
                }

            }
        }
    }

    public static void main(String[] args) {
        if (args.length == 2) {
            getUniformDirectedGraph(Integer.parseInt(args[0]), Float.parseFloat(args[1]));
        } else if (args.length == 3) {
            getPartitionedDirectedGraph(Integer.parseInt(args[0]), Float.parseFloat(args[1]), Integer
                    .parseInt(args[2]));
        }
    }

}
