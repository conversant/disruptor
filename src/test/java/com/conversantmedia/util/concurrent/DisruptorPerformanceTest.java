package com.conversantmedia.util.concurrent;

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

import java.text.DecimalFormat;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    private static final int MAXCAP = 512;
    // increase this number for a better performance test
    private static final int NFEED = 4*1*MAXCAP;

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
        final BlockingQueue<Integer> queue = new DisruptorBlockingQueue<Integer>(MAXCAP);

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

    @Ignore
    public synchronized  void testLinkedXferOneXOne() throws InterruptedException {
        final BlockingQueue<Integer> queue = new LinkedTransferQueue<Integer>();

        LOG.info("Linked Xfer 1x1 Poll");
        testNFeederByMReader(queue, 1, 1, true);
        LOG.info("Linked Xfer 1x1 Take");
        testNFeederByMReader(queue, 1, 1, false);
    }

    @Test
    public synchronized  void testDisruptorFourXFour() throws InterruptedException {
        final DisruptorBlockingQueue<Integer> queue = new DisruptorBlockingQueue<>(MAXCAP);

        for(int i=0; i<3; i++) {
            LOG.info("Disruptor 4x4 Poll");
            testNFeederByMReader(queue, 4, 4, true);
            LOG.info("Disruptor 4x4 Take");
            testNFeederByMReader(queue, 4, 4, false);
        }
    }

    @Test
    public synchronized  void testDisruptorEightXEight() throws InterruptedException {
        final DisruptorBlockingQueue<Integer> queue = new DisruptorBlockingQueue<>(MAXCAP);

        for(int i=0; i<3; i++) {
            LOG.info("Disruptor 4x4 Poll");
            testNFeederByMReader(queue, 8, 8, true);
            LOG.info("Disruptor 4x4 Take");
            testNFeederByMReader(queue, 8, 8, false);
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

        final DecimalFormat df = new DecimalFormat("#.##");

        final ExecutorService executor = Executors.newFixedThreadPool(n+m);


        int readCount = 0;
        int writeCount = 0;

        final Feeder[] feeder = new Feeder[n];
        final Reader[] reader = new Reader[m];

        final long startNanos = System.nanoTime();

        for(int i=0; i<n; i++) {
            feeder[i] = new Feeder(queue);
            executor.execute(feeder[i]);
        }

        for(int i=0; i<m; i++) {
            if(pollReader)
                reader[i] = new PollReader(queue);
            else
                reader[i] = new TakeReader(queue);

            executor.execute(reader[i]);
        }

        // finish feeding
        for(int i=0; i<n; i++)
            while(feeder[i].isAlive) Thread.yield();

        // reap readers
        for(int i=0; i<m; i++)
            while(reader[i].isAlive) Thread.yield();

        for(int i=0; i<reader.length; i++) {
            readCount += reader[i].readCount;
        }

        final long runTime = System.nanoTime()-startNanos;

        for(int i=0; i<feeder.length; i++) {
            writeCount += feeder[0].writeCount;
        }

        Assert.assertEquals(readCount,writeCount);
        System.out.println("  Runtime: " + df.format(runTime / 1e3) + "us");
        System.out.println("  Rate: " + df.format(writeCount * 1e6 / runTime) + " feed/ms");

        executor.shutdown();
    }



    private final static class Feeder implements Runnable {
        private final BlockingQueue<Integer> feedQueue;

        private int writeCount = 0;

        volatile boolean isAlive = true;

        static final Integer writeValue = Integer.valueOf(777);

        public Feeder(final BlockingQueue<Integer> feedQueue) {
            this.feedQueue = feedQueue;
        }

        @Override
        public void run() {

            int count = 0;
            while(count<NFEED) {
                // allocate my slot
                // do feed
                while(!feedQueue.offer(writeValue)) {
                    Condition.onSpinWait();
                }

                count++;
            }
            writeCount = count;

            isAlive = false;
        }
    }

    private static abstract class Reader implements Runnable {

        protected final BlockingQueue<Integer> readQueue;
        private static int nReader = 0;
        protected int readCount = 0;

        volatile boolean isAlive = true;

        public Reader(final BlockingQueue<Integer> readQueue) {
            this.readQueue = readQueue;
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
                while(readQueue.poll() != Feeder.writeValue) {
                    // failed try again
                    Condition.onSpinWait();
                }

                count++;
            }

            readCount = count;

            isAlive = false;
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
                    while(readQueue.take() != Feeder.writeValue) {
                        // failed try again
                        Condition.onSpinWait();
                    }

                    count++;
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            readCount = count;

            isAlive = false;
        }
    }

}
