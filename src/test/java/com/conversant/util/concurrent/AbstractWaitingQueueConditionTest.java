package com.conversant.util.concurrent;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by jcairns on 12/11/14.
 */
public class AbstractWaitingQueueConditionTest {

    private volatile boolean isCondition = true;

    @Before
    public void setup() {
        isCondition = true;
    }

    @Test
    public void testWaitNanos() throws InterruptedException {
        final TestQueueCondition tqc = new TestQueueCondition();

        final long startTime = System.nanoTime();
        tqc.awaitNanos(10_000_000L);
        final long waitTime = System.nanoTime() - startTime;

        Assert.assertTrue(waitTime > 10_000_000L);
    }


    @Test
    public void testAwait() throws InterruptedException {
        final TestQueueCondition tqc = new TestQueueCondition();

        final long startTime = System.nanoTime();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch(InterruptedException e) {

                }
                isCondition = false;
                tqc.signal();
            }
        }).start();

        tqc.await();

        final long waitTime = System.nanoTime() - startTime;

        Assert.assertTrue(waitTime > 500_000_000L);

    }


    @Test
    public void testAwaitNanos() throws InterruptedException {
        final TestQueueCondition tqc = new TestQueueCondition();

        final long startTime = System.nanoTime();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch(InterruptedException e) {

                }
                isCondition = false;
                tqc.signal();
            }
        }).start();

        tqc.awaitNanos(1_000_000_000L);

        final long waitTime = System.nanoTime() - startTime;

        Assert.assertTrue(waitTime > 500_000_000L);
        Assert.assertTrue(waitTime < 1_000_000_000L);

    }


    final class TestQueueCondition extends AbstractWaitingQueueCondition {

        @Override
        public boolean test() {
            return isCondition;
        }
    }



}
