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
package org.objectweb.proactive.extensions.timitspmd.util.charts;

import org.jdom.Element;
import org.objectweb.proactive.extensions.timitspmd.config.ConfigChart;
import org.objectweb.proactive.extensions.timitspmd.util.BenchmarkStatistics;


/**
 * This class represent a chart to generate with specific legend format and
 * scale mode.
 *
 * @author The ProActive Team
 */
public interface Chart extends java.io.Serializable {

    /**
     * The values in legend can be formated with different style :
     * <ul>
     * <li>DEFAULT is chart's dependant
     * <li>NONE will operate no change on the value. Example: 248 become 248
     * <li>K1000 will use power of 10. Example: 1024 become 1.02K, 1024000
     * become 1.02M
     * <li>K1024 will use power of 2. Example: 1024 become 1.00K, 1024000
     * become 977K
     * </ul>
     */
    public static enum LegendFormat {
        DEFAULT, NONE, POW10, POW2;
    };

    /**
     * The chart can have a specific scale:
     * <ul>
     * <li>DEFAULT is chart's dependant
     * <li>LINEAR create a chart with a linear scale
     * <li>LOGARITHMIC create a chart with a logarithmic scale
     * </ul>
     */
    public static enum Scale {
        DEFAULT, LINEAR, LOGARITHMIC;
    };

    /**
     * Generate a chart from the root Element of finalized benchmark serie, or a
     * BenchmarkStatistics if values can't be store in a convenient way in XML
     * serie result file (like communication pattern event datas)
     *
     * @param eTimit
     *            the root Element of the XML serie result file
     * @param bstats
     *            the BenchmarkStatistics computed by TimIt Reductor
     * @param cChart
     *            the parameters of the chart
     */
    public void generateChart(Element eTimit, BenchmarkStatistics bstats, ConfigChart cChart);
}
