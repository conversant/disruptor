package com.conversantmedia.util.concurrent;

import org.junit.Ignore;
import com.conversantmedia.util.estimation.Percentile;

/**
 * Created by jcairns on 5/29/14.
 */
@Ignore
public class ConcurrentQueuePerformanceTest {

    // increase this number for a legit performance test
    static final int NRUN = 20*1*1024;
    static final int NTHREAD = 4;

    private static final Integer INTVAL = 173;
    private static final long[] offerPctTimes = new long[NRUN];
    private static final long[] pollPctTimes = new long[NRUN];
    private static final long[] totPctTimes = new long[NRUN];

    private static final Integer[] first1024 = new Integer[1024];

    static {
        for(int i=0; i<1024; i++) {
            first1024[i] = i;
        }
    }

    @Ignore
    public static void testPerformance(final ConcurrentQueue<Integer> rb) throws InterruptedException, Percentile.InsufficientSamplesException {
        for(int c=0; c<3; c++) {
            System.gc();
            runPerformance(rb);
        }
    }

    @Ignore
    public static void runPerformance(final ConcurrentQueue<Integer> rb) throws InterruptedException, Percentile.InsufficientSamplesException {

        final int size = 1024;
        final long mask = size-1;

        final Percentile offerPct = new Percentile();
        final Percentile pollPct = new Percentile();
        final Percentile totPct = new Percentile();

        final Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                int i = NRUN;
                do {
                    final long startTime = System.nanoTime();

                    while (!rb.offer(first1024[(int) (startTime>>10 & mask)])) {
                        Thread.yield();
                    }
                    offerPctTimes[i-1] = System.nanoTime()-startTime;
                } while (i-- > 1);

                for(int d=0; d<offerPctTimes.length; d++) {
                    offerPct.add(offerPctTimes[d] / 1e3F);
                }

            }
        });

        thread.start();
        Integer result;
        int i = NRUN;

        do {
            final long startTime = System.nanoTime();

            while ((result = rb.poll()) == null) {
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
    public static void testRate(final ConcurrentQueue<Integer> rb) throws InterruptedException, Percentile.InsufficientSamplesException {
        for(int c=0; c<5; c++) {
            System.gc();
            runRate(rb);
        }
    }

    @Ignore
    public static void runRate(final ConcurrentQueue<Integer> rb) throws InterruptedException, Percentile.InsufficientSamplesException {
        final int size = 1024;


        final Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                int i = NRUN;
                do {
                    while (!rb.offer(INTVAL)) {
                        Thread.yield();
                    }
                } while (i-- != 0);
            }
        });


        final long startTime = System.nanoTime();

        thread.start();
        Integer result;
        int i = NRUN;
        do {
            while ((result = rb.poll()) == null) {
                Thread.yield();
            }

        } while (i-- != 0);

        thread.join();
        final long runTime = System.nanoTime() - startTime;
        System.out.println(Integer.toString(NRUN)+" in "+String.format("%3.1f ms", runTime/1e6)+": "+String.format("%d ns", runTime/NRUN));
    }


    @Ignore
    public static void testNThreadPerformance(final ConcurrentQueue<Integer> rb) throws InterruptedException, Percentile.InsufficientSamplesException {
        for(int c=0; c<3; c++) {
            System.gc();
            runNThreadPerformance(rb);
        }
    }

    @Ignore
    public static void runNThreadPerformance(final ConcurrentQueue<Integer> rb) throws InterruptedException, Percentile.InsufficientSamplesException {

        final int size = 1024;
        final long mask = size-1;

        final Percentile offerPct = new Percentile();
        final Percentile pollPct = new Percentile();
        final Percentile totPct = new Percentile();

        final Thread[] thread = new Thread[NTHREAD];

        for(int t = 0; t<NTHREAD; t++) {
            thread[t] = new Thread(new Runnable() {

                @Override
                public void run() {
                    int i = NRUN/NTHREAD;
                    do {
                        final long startTime = System.nanoTime();

                        while(!rb.offer(first1024[(int) (startTime>>10 & mask)])) {
                            Thread.yield();
                        }
                        offerPctTimes[i-1] = System.nanoTime()-startTime;
                    } while(i-->1);

                    for(int d = 0; d<offerPctTimes.length; d++) {
                        offerPct.add(offerPctTimes[d]/1e3F);
                    }

                }
            });
            thread[t].start();
        }


        Integer result;
        int i = NRUN;

        do {
            final long startTime = System.nanoTime();

            while ((result = rb.poll()) == null) {
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
