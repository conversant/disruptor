package com.conversant.util.concurrent;

import org.junit.Ignore;
import org.junit.Test;

import com.conversant.util.estimation.Percentile;

/**
 * Created by jcairns on 5/28/14.
 */
@Ignore
public class PushPullConcurrentQueuePerfTest {

    @Test
    public void testPerformance() throws InterruptedException, Percentile.InsufficientSamplesException {
        System.out.println("PushPull");
        ConcurrentQueuePerformanceTest.testPerformance(new PushPullConcurrentQueue<Integer>(64));
    }


    @Test
    public void testRate() throws InterruptedException, Percentile.InsufficientSamplesException {
        ConcurrentQueuePerformanceTest.testRate(new PushPullConcurrentQueue<Integer>(64));
    }

}
