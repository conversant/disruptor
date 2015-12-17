package com.conversant.util.concurrent;

import org.junit.Test;

import com.conversant.util.estimation.Percentile;

/**
 * Created by jcairns on 5/28/14.
 */
public class DisruptorQueuePerfTest {

	@Test
	public void testPerformance() throws InterruptedException, Percentile.InsufficientSamplesException {
		System.out.println("Disruptor");
		ConcurrentQueuePerformanceTest.testPerformance(new DisruptorBlockingQueue<Integer>(64));
	}


	@Test
	public void testRate() throws InterruptedException, Percentile.InsufficientSamplesException {
		ConcurrentQueuePerformanceTest.testRate(new DisruptorBlockingQueue<Integer>(64));
	}

}
