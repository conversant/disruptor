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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by jcairns on 12/11/14.
 */
public class AbstractWaitingConditionTest {

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


    final class TestQueueCondition extends AbstractWaitingCondition {

        @Override
        public boolean test() {
            return isCondition;
        }
    }



}
