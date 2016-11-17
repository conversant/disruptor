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

import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.conversantmedia.util.estimation.Percentile;

/**
 * Created by jcairns on 5/29/14.
 */
@Ignore
public class DisruptorPerformanceTest2 {

    static final int QUEUE_SIZE = 64;
    // increase this number for a legit performance test
    static final int NRUN = 4*1*1024;
    static final int NTHREAD = 4;

    private static final Integer INTVAL = 173;
    private static final long[] pollPctTimes = new long[NRUN];
    private static final long[] totPctTimes = new long[NRUN];

    private static final Integer[] firstN = new Integer[QUEUE_SIZE];

    static {
        for(int i=0; i<QUEUE_SIZE; i++) {
            firstN[i] = i;
        }
    }

    @Test
    public void testPerformance() throws InterruptedException, Percentile.InsufficientSamplesException {
        System.out.println("1x1 using MultiThread");
        testPerformance(new DisruptorBlockingQueue<>(QUEUE_SIZE));
    }


    @Test
    public void testRate() throws InterruptedException, Percentile.InsufficientSamplesException {
        testRate(new DisruptorBlockingQueue<>(QUEUE_SIZE));
    }

    @Test
    public void testNThreadPerformance() throws Percentile.InsufficientSamplesException, InterruptedException {
        System.out.println("NThread in MultiThread");
        testNThreadPerformance(new DisruptorBlockingQueue<>(QUEUE_SIZE));
    }

    @Ignore
    public static void testPerformance(final BlockingQueue<Integer> rb) throws InterruptedException, Percentile.InsufficientSamplesException {
        for(int c=0; c<3; c++) {
            System.gc();
            runPerformance(rb);
        }
    }

    @Ignore
    public static void runPerformance(final BlockingQueue<Integer> rb) throws InterruptedException, Percentile.InsufficientSamplesException {

        final long mask = QUEUE_SIZE-1;

        final Percentile offerPct = new Percentile();
        final Percentile pollPct = new Percentile();
        final Percentile totPct = new Percentile();

        final Thread thread = new Thread(new Runnable() {
            private final long[] offerPctTimes = new long[NRUN];

            @Override
            public void run() {
                try {
                    int i = NRUN;
                    do {
                        final long startTime = System.nanoTime();

                        while(!rb.offer(firstN[(int) (startTime>>10 & mask)], 50, TimeUnit.MILLISECONDS)) {
                            Thread.yield();
                        }
                        offerPctTimes[i-1] = System.nanoTime()-startTime;
                    } while(i-->1);

                    for(int d = 0; d<offerPctTimes.length; d++) {
                        offerPct.add(offerPctTimes[d]/1e3F);
                    }
                } catch(InterruptedException ex) {
                    System.out.println("Interrupted, no data");
                }

            }
        });

        thread.start();
        Integer result;
        int i = NRUN;

        do {
            final long startTime = System.nanoTime();

            while ((result = rb.poll(50L, TimeUnit.MILLISECONDS)) == null) {
                Thread.yield();
            }

            final int diff = (int) (System.nanoTime()>>10 & mask) - result.intValue();

            totPctTimes[i-1] = diff;

            pollPctTimes[i-1] = System.nanoTime() - startTime;
        } while (i-- > 1);

        for(int d=0; d<pollPctTimes.length; d++) {
            pollPct.add(pollPctTimes[d] / 1e3F);
        }

        for(int d=0; d<totPctTimes.length; d++) {
            if(totPctTimes[d] > 0) {
                totPct.add(totPctTimes[d]);
            }
        }

        thread.join();

        Percentile.print(System.out, "offer (us):", offerPct);
        Percentile.print(System.out, "poll (us): ", pollPct);
        Percentile.print(System.out, "tot (~us): ", totPct);
    }

    @Ignore
    public static void testRate(final BlockingQueue<Integer> rb) throws InterruptedException, Percentile.InsufficientSamplesException {
        for(int c=0; c<5; c++) {
            System.gc();
            runRate(rb);
        }
    }

    @Ignore
    public static void runRate(final BlockingQueue<Integer> rb) throws InterruptedException, Percentile.InsufficientSamplesException {
        final int size = 1024;


        final Thread thread = new Thread(() -> {
            try {
                int i = NRUN;
                do {
                    while(!rb.offer(INTVAL, 50, TimeUnit.MILLISECONDS)) {
                        Thread.yield();
                    }
                } while(i-- != 0);
            } catch(InterruptedException ex) {
                System.out.println("Interrupted no data");
            }
        });


        final long startTime = System.nanoTime();

        thread.start();
        int i = NRUN;
        do {
            while (rb.poll(50L, TimeUnit.MILLISECONDS) != INTVAL) {
                Thread.yield();
            }

        } while (i-- != 0);

        thread.join();
        final long runTime = System.nanoTime() - startTime;
        System.out.println(Integer.toString(NRUN)+" in "+String.format("%3.1f ms", runTime/1e6)+": "+String.format("%d ns", runTime/NRUN));
    }


    @Ignore
    public static void testNThreadPerformance(final BlockingQueue<Integer> rb) throws InterruptedException, Percentile.InsufficientSamplesException {
        for(int c=0; c<3; c++) {
            System.gc();
            runNThreadPerformance(rb);
        }
    }

    @Ignore
    public static void runNThreadPerformance(final BlockingQueue<Integer> rb) throws InterruptedException, Percentile.InsufficientSamplesException {

        final long mask = QUEUE_SIZE-1;

        final Percentile offerPct = new Percentile();
        final Percentile pollPct = new Percentile();
        final Percentile totPct = new Percentile();

        final Thread[] thread = new Thread[NTHREAD];

        for(int t = 0; t<NTHREAD; t++) {
            thread[t] = new Thread(new Runnable() {
                private final long[] offerPctTimes = new long[NRUN];

                @Override
                public void run() {
                    try {
                        int i = NRUN/NTHREAD;
                        do {
                            final long startTime = System.nanoTime();

                            while(!rb.offer(firstN[(int) (startTime>>10 & mask)], 50L, TimeUnit.MILLISECONDS)) {
                                Thread.yield();
                            }
                            offerPctTimes[i-1] = System.nanoTime()-startTime;
                        } while(i-->1);

                        for(int d = 0; d<offerPctTimes.length; d++) {
                            synchronized (offerPct) {
                                offerPct.add(offerPctTimes[d]/1e3F);
                            }
                        }
                    } catch(InterruptedException ex) {

                    }
                }
            });
            thread[t].start();
        }


        Integer result;
        int i = NRUN;

        do {
            final long startTime = System.nanoTime();

            while ((result = rb.poll(50L, TimeUnit.MILLISECONDS)) == null) {
                Thread.yield();
            }

            final int diff = (int) (System.nanoTime()>>10 & mask) - result.intValue();

            totPctTimes[i-1] = diff;

            pollPctTimes[i-1] = System.nanoTime() - startTime;
        } while (i-- > 1);

        for(int d=0; d<pollPctTimes.length; d++) {
            pollPct.add(pollPctTimes[d] / 1e3F);
        }

        for(int d=0; d<totPctTimes.length; d++) {
            if(totPctTimes[d] > 0) {
                totPct.add(totPctTimes[d]);
            }
        }

        Percentile.print(System.out, "offer (us):", offerPct);
        Percentile.print(System.out, "poll (us): ", pollPct);
        Percentile.print(System.out, "tot (~us): ", totPct);

        for(int t=0; t<NTHREAD; t++) {
            thread[t].join();
        }
    }


}
