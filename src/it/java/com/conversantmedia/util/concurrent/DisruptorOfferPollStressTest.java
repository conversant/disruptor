package com.conversantmedia.util.concurrent;

import com.conversantmedia.util.concurrent.DisruptorBlockingQueue;
import com.conversantmedia.util.concurrent.SpinPolicy;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by jcairns on 2/23/16.
 */
public class DisruptorOfferPollStressTest {

    private static final int QUEUE_SZ = 1024;
    private static final int NTHREAD = 4;

    public static final long TIMEOUT = 200_000L;
    public static final int MIN_PROGRESS = 10;
    public static final long MIN_RUN_TIME = 3000L;

    private BlockingQueue<Long> msgQueue;

    private volatile boolean isRunning;

    private final ExecutorService executor = Executors.newFixedThreadPool(NTHREAD);


    @Before
    public void setup() {
        msgQueue = new DisruptorBlockingQueue<Long>(QUEUE_SZ, SpinPolicy.WAITING);
        isRunning = true;
    }

    @After
    public void tearDown() {
        executor.shutdownNow();
    }

    @Test(timeout=TIMEOUT)
    public void testOfferPollProgress() throws InterruptedException {

        ProgressCheck[] check = new ProgressCheck[2];
        executor.execute(new PutEvens());
        Thread.sleep(1000L);
        executor.execute(check[0] = new PollEven());
        Thread.sleep(1000L);
        executor.execute(new PutOdds());
        Thread.sleep(1000L);
        executor.execute(check[1] = new TakeOdd());

        final long endTime = System.currentTimeMillis() + MIN_RUN_TIME;

        boolean allProgressed = true;
        do {
            for (int i = 0; i < 2; i++) {
                if (!check[i].madeProgress()) {
                    allProgressed = false;
                    Thread.yield();
                }
            }
        } while(!allProgressed && (endTime > System.currentTimeMillis()));

        allProgressed = true;
        for (int i = 0; i < 2; i++) {
            if (check[i].madeProgress()) {
                System.out.println(i + " made progress");
            } else {
                allProgressed = false;
                System.out.println(i + " did not progress");
            }
        }


        System.out.println();

        Assert.assertTrue(allProgressed);

    }


    private class PutEvens implements Runnable {

        @Override
        public void run() {
            while(isRunning) {
                for(long i=0; i<Integer.MAX_VALUE && isRunning; i+=2L) {
                    try {
                        while(!msgQueue.offer(i, 1L, TimeUnit.MILLISECONDS)) {
                            Thread.yield();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        isRunning=false;
                    }
                }
            }
        }
    }

    private class PutOdds implements Runnable {

        @Override
        public void run() {
            while(isRunning) {
                for(long i=1; i<Integer.MAX_VALUE && isRunning; i+=2L) {
                    try {
                        while(!msgQueue.offer(i, 1L, TimeUnit.MILLISECONDS)) {
                            Thread.yield();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        isRunning=false;
                    }
                }
            }
        }
    }

    private interface ProgressCheck extends Runnable  {
        boolean madeProgress();
    }

    private class PollEven implements ProgressCheck {
        private volatile long madeProgress = -1;

        @Override
        public void run() {
            Long r = 0L;
            while(isRunning) {
                final Long v;
                try {
                    final Long peek = msgQueue.peek();
                    if(peek != null && (peek.longValue() & 1) == 0) {
                        // ours
                        v = msgQueue.poll(1L, TimeUnit.MILLISECONDS);
                        if(v!= null && v.equals(r)) {
                            madeProgress++;
                            r = v+2;
                            if(r > Integer.MAX_VALUE) {
                                r = 0L;
                            }
                        }
                    }

                } catch (InterruptedException e) {
                    isRunning = false;
                }
            }
        }

        @Override
        public boolean madeProgress() {
            return madeProgress > MIN_PROGRESS;
        }
    }

    private class TakeOdd implements ProgressCheck {
        private volatile long madeProgress = -1;

        @Override
        public void run() {
            Long r = 1L;
            while(isRunning) {
                final Long v;
                try {
                    final Long peek = msgQueue.peek();
                    if(peek != null && (peek.longValue() & 1) == 1) {
                        // ours
                        v = msgQueue.poll(1L, TimeUnit.MILLISECONDS);
                        if(v != null && v.equals(r)) {
                            madeProgress++;
                            r = v+2;
                            if(r > Integer.MAX_VALUE) {
                                r = 0L;
                            }
                        }
                    }

                } catch (InterruptedException e) {
                    isRunning = false;
                }
            }
        }

        @Override
        public boolean madeProgress() {
            return madeProgress > MIN_PROGRESS;
        }
    }



}
