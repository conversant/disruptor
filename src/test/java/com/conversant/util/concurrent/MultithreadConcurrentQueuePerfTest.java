package com.conversant.util.concurrent;

import org.junit.Ignore;
import org.junit.Test;

import com.conversant.util.estimation.Percentile;

/**
 * Created by jcairns on 5/28/14.
 */
@Ignore
public class MultithreadConcurrentQueuePerfTest {

    @Test
    public void testPerformance() throws InterruptedException, Percentile.InsufficientSamplesException {
        System.out.println("1x1 using MultiThread");
        ConcurrentQueuePerformanceTest.testPerformance(new MultithreadConcurrentQueue<Integer>(1024));
    }


    @Test
    public void testRate() throws InterruptedException, Percentile.InsufficientSamplesException {
        ConcurrentQueuePerformanceTest.testRate(new MultithreadConcurrentQueue<Integer>(1024));
    }

    @Test
    public void testNThreadPerformance() throws Percentile.InsufficientSamplesException, InterruptedException {
        System.out.println("NThread in MultiThread");
        ConcurrentQueuePerformanceTest.testNThreadPerformance(new MultithreadConcurrentQueue<Integer>(1024));
    }

}
