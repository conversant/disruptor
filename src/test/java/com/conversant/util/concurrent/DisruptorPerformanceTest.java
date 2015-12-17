package com.conversant.util.concurrent;

import java.text.DecimalFormat;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author John Cairns <jcairns@dotomi.com> Date: 4/26/12 Time: 8:09 AM
 */
@Ignore
public class DisruptorPerformanceTest {

	private static final Logger LOG = LoggerFactory.getLogger(DisruptorPerformanceTest.class);
	private static final int MAXCAP = 1024;
	// increase this number for a better performance test
	private static final int NFEED = 1*MAXCAP;

	@Ignore
	public  synchronized void testArrayOneXOne() throws InterruptedException {
		final BlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(MAXCAP);

		LOG.info("Array 1x1 Poll");
		testNFeederByMReader(queue, 1, 1, true);
		LOG.info("Array 1x1 Take");
		testNFeederByMReader(queue, 1, 1, false);
	}

	@Test
	public synchronized  void testDisruptorOneXOne() throws InterruptedException {
		final BlockingQueue<Integer> queue = new DisruptorBlockingQueue<Integer>(MAXCAP, true);

		for(int i=0; i<3; i++) {
			LOG.info("Disruptor 1x1 Poll");
			testNFeederByMReader(queue, 1, 1, true);
			LOG.info("Disruptor 1x1 Take");
			testNFeederByMReader(queue, 1, 1, false);
		}

	}

	@Ignore
	public  synchronized void testLinkedBlockOneXOne() throws InterruptedException {
		final BlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>(MAXCAP);

		LOG.info("Linked B 1x1 Poll");
		testNFeederByMReader(queue, 1, 1, true);
		LOG.info("Linked B 1x1 Take");
		testNFeederByMReader(queue, 1, 1, false);
	}

	@Test
	public synchronized  void testLinkedXferOneXOne() throws InterruptedException {
		final BlockingQueue<Integer> queue = new LinkedTransferQueue<Integer>();

		LOG.info("Linked Xfer 1x1 Poll");
		testNFeederByMReader(queue, 1, 1, true);
		LOG.info("Linked Xfer 1x1 Take");
		testNFeederByMReader(queue, 1, 1, false);
	}

	@Test
	public synchronized  void testDisruptorFourXFour() throws InterruptedException {
		final DisruptorBlockingQueue<Integer> queue = new DisruptorBlockingQueue<Integer>(MAXCAP, true);

		for(int i=0; i<3; i++) {
			LOG.info("Disruptor 4x4 Poll");
			testNFeederByMReader(queue, 4, 4, true);
			LOG.info("Disruptor 4x4 Take");
			testNFeederByMReader(queue, 4, 4, false);
		}
	}

	@Ignore
	public  synchronized void testLinkedBEightXEight() throws InterruptedException {
		final BlockingQueue<Integer> queue = new LinkedBlockingQueue();

		for(int i=0; i<3; i++) {

			LOG.info("Linked B 8x8 Poll");
			testNFeederByMReader(queue, 8, 8, true);
			LOG.info("Linked B 8x8 Take");
			testNFeederByMReader(queue, 8, 8, false);
		}
	}

	@Test
	public  synchronized void testLinkedFourXFour() throws InterruptedException {
		final BlockingQueue<Integer> queue = new LinkedTransferQueue();

		for(int i=0; i<3; i++) {
			LOG.info("Linked Xfer 4x4 Poll");
			testNFeederByMReader(queue, 4, 4, true);
			LOG.info("Linked Xfer 4x4 Take");
			testNFeederByMReader(queue, 4, 4, false);
		}
	}

	@Ignore
	public  synchronized void testArrayEightXEight() throws InterruptedException {
		final BlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(MAXCAP);

		LOG.info("Array 8x8 Poll");
		testNFeederByMReader(queue, 8, 8, true);
		LOG.info("Array 8x8 Take");
		testNFeederByMReader(queue, 8, 8, false);
	}


	@Ignore
	private static synchronized void testNFeederByMReader(final BlockingQueue<Integer> queue, final int n, final int m, final boolean pollReader) throws InterruptedException {

		DecimalFormat df = new DecimalFormat("#.##");

		int readCount = 0;
		int writeCount = 0;

		final Feeder[] feeder = new Feeder[n];
		final Reader[] reader = new Reader[m];
		for(int i=0; i<n; i++) {
			feeder[i] = new Feeder(queue);
		}

		for(int i=0; i<m; i++) {
			if(pollReader)
				reader[i] = new PollReader(queue);
			else
				reader[i] = new TakeReader(queue);
		}

		final long startNanos = System.nanoTime();

		if(n<m) {
			for(int i=0; i<n; i++) {
				feeder[i].start();
				reader[i].start();
			}

			for(int i=n; i<m; i++) {
				reader[i].start();
			}
		} else {
			for(int i=0; i<m; i++) {
				feeder[i].start();
				reader[i].start();
			}

			for(int i=m; i<n; i++) {
				feeder[i].start();
			}

		}

		// wait for feeding to finish
		for(int i=0; i<n; i++)
			while(feeder[i].isAlive()) Thread.yield();

		// reap readers
		for(int i=0; i<m; i++) {
			if(!pollReader) {
				// make sure no takers are waiting for something to eat
				while(reader[i].isAlive()) {
					queue.offer(Integer.valueOf(0));
				}
			}

			reader[i].join();

		}

		for(int i=0; i<reader.length; i++) {
			readCount += reader[i].readCount;
		}

		final long runTime = System.nanoTime()-startNanos;

		for(int i=0; i<n; i++) {
			feeder[i].join();
		}

		for(int i=0; i<feeder.length; i++) {
			writeCount += feeder[0].writeCount;
		}

		Assert.assertEquals(readCount,writeCount);
		System.out.println("  Runtime: " + df.format(runTime / 1e3) + "us");
		System.out.println("  Rate: " + df.format(writeCount * 1e6 / runTime) + " feed/ms");

	}



	private final static class Feeder extends Thread {
		private static int nFeeder = 0;
		private final BlockingQueue<Integer> feedQueue;

		private int writeCount = 0;

		private static final Integer writeValue = Integer.valueOf(777);

		public Feeder(final BlockingQueue<Integer> feedQueue) {
			this.feedQueue = feedQueue;
			setName("Feeder-"+nFeeder++);
		}

		@Override
		public void run() {

			int count = 0;
			while(count<NFEED) {
				// allocate my slot
				// do feed
				while(!feedQueue.offer(writeValue)) {
					yield();
				}

				count++;
			}
			writeCount = count;
		}
	}

	private static abstract class Reader extends Thread {

		protected final BlockingQueue<Integer> readQueue;
		private static int nReader = 0;
		protected int readCount = 0;

		public Reader(final BlockingQueue<Integer> readQueue) {
			this.readQueue = readQueue;
			setName("Reader-"+nReader++);
		}

	}


	private final static class PollReader extends Reader {

		public PollReader(final BlockingQueue<Integer> readQueue) {
			super(readQueue);
		}

		@Override
		public void run() {
			int count=0;
			while(count < NFEED) {
				// we are allowed to read this slot
				Integer p;
				while((p=readQueue.poll()) == null) {
					// failed try again
					yield();
				}

				count++;
			}

			readCount = count;
		}

	}

	private final static class TakeReader extends Reader {

		public TakeReader(final BlockingQueue<Integer> readQueue) {
			super(readQueue);
		}

		@Override
		public void run() {
			int count=0;
			while(count < NFEED) {
				try {
					// we are allowed to read this slot
					while(readQueue.take() == null) {
						// failed try again
						yield();
					}

					count++;
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			readCount = count;
		}

	}

}
