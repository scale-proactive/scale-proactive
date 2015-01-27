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
package functionalTests.multiactivities.imageprocessing;

public class BitmapRegion implements java.io.Serializable {

    private String bitmapName;
    private int x;
    private int y;
    private int width;
    private int height;

    public BitmapRegion(String bitmapName, int x, int y, int width, int height) {
        super();
        this.bitmapName = bitmapName;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public String getBitmapName() {
        return bitmapName;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean overlaps(BitmapRegion other) {
        if (!other.getBitmapName().equals(bitmapName)) {
            return false;
        }

        int centX1 = x + width / 2;
        int centY1 = y + height / 2;
        int centX2 = other.getX() + other.getWidth() / 2;
        int centY2 = other.getY() + other.getHeight() / 2;
        return (Math.abs(centX1 - centX2) < (other.getWidth() / 2 + width / 2)) ||
            (Math.abs(centY1 - centY2) < (other.getHeight() / 2 + height / 2));
    }

    @Override
    public String toString() {
        return bitmapName;
    }

    public static boolean sameName(Object param1, Object param2) {
        return param1.toString().equals(param2.toString());
    }

}
