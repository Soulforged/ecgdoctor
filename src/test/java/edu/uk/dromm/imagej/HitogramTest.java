/**
 * 
 */
package edu.uk.dromm.imagej;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author dicardo
 *
 */
public class HitogramTest implements PlugInFilter {

    private Map<Integer, Integer> histogramData;

    @Test
    public void process() {
        try {
            final URL acornImage = this.getClass().getResource(
                    "/image/ecg-output-lrg.jpg");
            Assert.assertNotNull(acornImage);
            final BufferedImage bi = ImageIO.read(acornImage);

            final ImageProcessor ip = new ColorProcessor(bi);
            run(ip);
            final Iterator<Entry<Integer, Integer>> it = histogramData
                    .entrySet().iterator();
            Integer value = 0;
            while (it.hasNext()) {
                final Entry<Integer, Integer> entry = it.next();
                value += entry.getValue();
                System.out.println(String.format("%s:%s",
                        Integer.toHexString(entry.getKey()), value));
            }
            System.out.println("Total: " + value);
        } catch (final IOException e) {
            Assert.fail(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * @see ij.plugin.filter.PlugInFilter#run(ij.process.ImageProcessor)
     */
    public void run(final ImageProcessor ip) {
        histogramData = new HashMap<Integer, Integer>(32000);
        final int width = ip.getWidth();
        final int height = ip.getHeight();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                final int k = ip.getPixel(j, i);
                Integer current = histogramData.get(k);
                current = current == null ? 0 : current;
                histogramData.put(k, current + 1);
            }
        }

    }

    /*
     * (non-Javadoc)
     * @see ij.plugin.filter.PlugInFilter#setup(java.lang.String, ij.ImagePlus)
     */
    public int setup(final String arg0, final ImagePlus arg1) {
        return NO_CHANGES;
    }
}
