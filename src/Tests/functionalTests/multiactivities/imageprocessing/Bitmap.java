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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;


public class Bitmap implements java.io.Serializable {

    private ArrayList<Integer> red;
    private ArrayList<Integer> blue;
    private ArrayList<Integer> green;
    private int width;
    private int height;

    public Bitmap(int width, int height) {
        red = new ArrayList<Integer>(width * height);
        blue = new ArrayList<Integer>(width * height);
        green = new ArrayList<Integer>(width * height);
        for (int x = 0; x < width * height; x++) {
            red.add(x, x);
            green.add(x, x);
            blue.add(x, x);
        }
        this.width = width;
        this.height = height;
    }

    public int[] getRGB(int x, int y) {
        int[] res = { red.get(y * width + x), green.get(y * width + x), blue.get(y * width + x) };
        return res;
    }

    public void setRGB(int x, int y, int[] pixel) {
        red.set(y * width + x, pixel[0]);
        green.set(y * width + x, pixel[1]);
        blue.set(y * width + x, pixel[2]);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public static Bitmap loadBitmap(String file) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(file));
            Bitmap bitmap = new Bitmap(img.getWidth(), img.getHeight());
            for (int x = 0; x < img.getWidth(); x++) {
                for (int y = 0; y < img.getHeight(); y++) {
                    byte[] pixel = new byte[3];
                    pixel[0] = (byte) img.getData().getSample(x, y, 0);
                    pixel[1] = (byte) img.getData().getSample(x, y, 1);
                    pixel[2] = (byte) img.getData().getSample(x, y, 2);
                }
            }

            return bitmap;

        } catch (IOException e) {
        }

        return null;
    }

    public static void saveBitmap(String file) {
        //TODO
    }
}
