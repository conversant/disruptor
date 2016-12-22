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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by jcairns on 8/5/14.
 */
public class MultithreadConcurrentQueueTest {

    private static final int TEST_SIZE = 10*1024;

    private ThreadPoolExecutor executor;
    private MultithreadConcurrentQueue<Integer> queue;

    private final Integer a1 = Integer.valueOf(1);
    private final Integer a2 = Integer.valueOf(2);
    private final Integer a3 = Integer.valueOf(3);
    private final Integer a4 = Integer.valueOf(4);
    private final Integer a5 = Integer.valueOf(5);

    private int c1, c2, c3, c4, c5;


    @Before
    public void setup() {
        executor = new ThreadPoolExecutor(5, 5, 1, TimeUnit.MINUTES, new DisruptorBlockingQueue<>(1024));

        queue = new MultithreadConcurrentQueue<>(1024);

        c1 = c2 = c3 = c4 = c5 = 0;
    }

    @After
    public void teardown() {
        executor.shutdownNow();
    }

    private class OfferTask implements Runnable {
        private final Integer offering;

        public OfferTask(Integer offering) {
            this.offering = offering;
        }

        @Override
        public void run() {
            for(int i=0; i<TEST_SIZE; i++) {
                while(!queue.offer(offering));
            }
        }
    }

    @Test(timeout=60000L)
    public void testManyThreadsOffer() {
        executor.execute(new OfferTask(a1));
        executor.execute(new OfferTask(a2));
        executor.execute(new OfferTask(a3));
        executor.execute(new OfferTask(a4));
        executor.execute(new OfferTask(a5));

        while(c1+c2+c3+c4+c5 < 5*TEST_SIZE) {
            Integer a = queue.poll();
            while(a == null) {
                a = queue.poll();
            }

            if(a == a1) {
                c1++;
            } else if(a == a2) {
                c2++;
            } else if(a == a3) {
                c3++;
            } else if(a == a4) {
                c4++;
            } else if(a == a5) {
                c5++;
            } else {
                Assert.fail();
            }
        }

        Assert.assertEquals(TEST_SIZE, c1);
        Assert.assertEquals(TEST_SIZE, c2);
        Assert.assertEquals(TEST_SIZE, c3);
        Assert.assertEquals(TEST_SIZE, c4);
        Assert.assertEquals(TEST_SIZE, c5);
    }

    @Test(timeout=60000L)
    public void testManyPoll() {
        final AtomicReference<Integer> nextOne = new AtomicReference<>(a1);
        final int NUM_POLLERS = 5;
        final Runnable task = () -> {
            while(c1+c2+c3+c4+c5 < NUM_POLLERS*TEST_SIZE) {
                Integer a = queue.poll();
                while(a == null) {
                    a = queue.poll();
                }

                while(nextOne.get() != a) Thread.yield();

                if(a == a1) {
                    c1++;
                    nextOne.set(a2);
                } else if(a == a2) {
                    c2++;
                    nextOne.set(a3);
                } else if(a == a3) {
                    c3++;
                    nextOne.set(a4);
                } else if(a == a4) {
                    c4++;
                    nextOne.set(a5);
                } else if(a == a5) {
                    c5++;
                    nextOne.set(a1);
                }
            }
        };

        for(int i = 0; i<NUM_POLLERS; i++) {
            executor.execute(task);
        }

        for(int i = 0; i<TEST_SIZE; i++) {
            // order will always be a1, a2, a3, a4, a5
            while(!queue.offer(a1)) ;
            while(!queue.offer(a2)) ;
            while(!queue.offer(a3)) ;
            while(!queue.offer(a4)) ;
            while(!queue.offer(a5)) ;
        }

        while(c1+c2+c3+c4+c5 < NUM_POLLERS*TEST_SIZE) {
            Thread.yield();
        }

        Assert.assertEquals(TEST_SIZE, c1);
        Assert.assertEquals(TEST_SIZE, c2);
        Assert.assertEquals(TEST_SIZE, c3);
        Assert.assertEquals(TEST_SIZE, c4);
        Assert.assertEquals(TEST_SIZE, c5);
    }
}
