package com.conversantmedia.util.estimation;

/*
 * #%L
 * Conversant Disruptor
 * ~~
 * Conversantmedia.com © 2016, Conversant, Inc. Conversant® is a trademark of Conversant, Inc.
 * ~~
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Random;

/**
 * Created by jcairns on 5/28/14.
 */
public class PercentileTest {

    // more precision from more samples
    private final float EPS = .01F;
    private final int NTRIALS = 100000;

    private Random r;


    @Before
    public void setup() {
        r = new Random(13);
    }


    @Test
    public void testPercentDistribution() throws Percentile.InsufficientSamplesException {

        final Percentile p = new Percentile();

        float min=1, max=0;

        for(int i=0; i<NTRIALS; i++) {
            final float f = r.nextFloat();
            p.add(f);

            if(f < min) min = f;
            if(f > max) max = f;
        }

        Assert.assertEquals(NTRIALS, p.getNSamples());

        final float[] qs = p.getQuantiles();
        final float[] e  = p.getEstimates();
        for(int i=0; i<qs.length; i++) {
            Assert.assertTrue(Float.toString(qs[i])+" != "+Float.toString(e[i]), approximatelyEqual(qs[i], e[i], EPS));
        }

        Assert.assertTrue("Min is "+min+" not "+p.getMin(), approximatelyEqual(min, p.getMin(), EPS));
        Assert.assertTrue("Max is "+max+" not "+p.getMax(), approximatelyEqual(max, p.getMax(), EPS));
    }

    @Test(expected=Percentile.InsufficientSamplesException.class)
    public void testNotReady() throws Percentile.InsufficientSamplesException {
        final Percentile p = new Percentile();

        Assert.assertFalse(p.isReady());

        p.getEstimates();
    }

    @Test
    public void testNSamples() {
        final Percentile p = new Percentile();

        for(int i=0; i<15000; i++) {
            p.add(i);

            Assert.assertEquals(i+1, p.getNSamples());
        }
    }


    @Ignore
    private final static boolean approximatelyEqual(final float a, final float b, final float eps) {
        return Math.abs(a - b) <= ( (Math.abs(a) < Math.abs(b) ? Math.abs(b) : Math.abs(a))*eps);
    }
}
