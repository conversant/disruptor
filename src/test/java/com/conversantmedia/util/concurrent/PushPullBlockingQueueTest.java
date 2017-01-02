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
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author John Cairns <jcairns@dotomi.com>
 *         Date: 4//25/12
 *         Time: 3:27 PM
 */
public class PushPullBlockingQueueTest {

    final static boolean ALLOW_LONG_RUN = false;

    @Test
    public void testPushPullBlockingQueueTestC1() {
        final int cap = 10;
        BlockingQueue<Integer> dbq = new PushPullBlockingQueue<Integer>(cap);
        while(dbq.offer(Integer.valueOf(0)));
        Assert.assertEquals(16, dbq.size());
    }

    @Test
    public void testPushPullBlockingQueueTestC2() {

        final int cap = 50;

        Set<Integer> x = new HashSet<Integer>(cap);
        for(int i=0; i<2*cap; i++) {
            x.add(Integer.valueOf(i));
        }

        BlockingQueue<Integer> dbq = new PushPullBlockingQueue<Integer>(cap, x);
        // next power of two
        Assert.assertEquals(64, dbq.size());
    }

    @Test
    public void testOffer() {

        final int cap = 16;
        BlockingQueue<Integer> dbq = new PushPullBlockingQueue<Integer>(cap);
        for(int i=0; i<cap; i++) {
            dbq.offer(Integer.valueOf(i));
        }

        Assert.assertFalse(dbq.offer(Integer.valueOf(cap)));

        for(int i=0; i<cap; i++) {
            Assert.assertEquals(Integer.valueOf(i), dbq.poll());
        }

    }



    @Test
    public void remove() {

        final int cap = 10;
        BlockingQueue<Integer> dbq = new PushPullBlockingQueue<Integer>(cap);
        for(int i=0; i<cap; i++) {
            dbq.offer(Integer.valueOf(i));
        }

        Integer i = dbq.peek();
        Integer x = dbq.remove();

        Assert.assertEquals(i, x);
        Assert.assertEquals(i, Integer.valueOf(0));
        Assert.assertFalse(i.equals(dbq.peek()));
    }

    @Test
    public void testPoll() {
        final int cap = 10;
        BlockingQueue<Integer> dbq = new PushPullBlockingQueue<Integer>(cap);

        Assert.assertNull(dbq.poll());

        dbq.offer(Integer.valueOf(1));
        dbq.offer(Integer.valueOf(2));
        Assert.assertEquals(dbq.poll(), Integer.valueOf(1));
        Assert.assertEquals(dbq.poll(), Integer.valueOf(2));

        Assert.assertNull(dbq.poll());
    }

    @Test
    public void testElement() {
        final int cap = 10;
        BlockingQueue<Integer> dbq = new PushPullBlockingQueue<Integer>(cap);

        try {
            dbq.element();
            Assert.fail();
        } catch(NoSuchElementException ex) {
            // expected
        }
    }


    @Test
    public void testPeek() {
        final int cap = 10;
        BlockingQueue<Integer> dbq = new PushPullBlockingQueue<Integer>(cap);

        try {

            Assert.assertNull(dbq.peek());

        } catch(NoSuchElementException nsex) {
            Assert.fail();
        }

        for(int i=0; i<cap; i++) {
            dbq.offer(Integer.valueOf(i));
            Assert.assertEquals(Integer.valueOf(0), dbq.peek());
        }

        for(int i=0; i<cap; i++) {
            Assert.assertEquals(Integer.valueOf(i), dbq.peek());
            dbq.poll(); // count up values checking peeks
        }
    }


    @Test
    public void testPut() throws InterruptedException {

        final int cap = 10;
        final BlockingQueue<Integer> dbq = new PushPullBlockingQueue<Integer>(cap);

        for(int i=0; i<cap; i++) {
            dbq.offer(Integer.valueOf(i));
        }

        new Thread(){
            @Override
            public void run() {
                try {
                    sleep(1000);
                    // after a second remove one
                    dbq.poll();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();


        // in main thread add one
        // this operation must wait for thread
        dbq.put(Integer.valueOf(cap));

        boolean hasValCap = false;
        while(!dbq.isEmpty()) {
            if(dbq.poll().equals(Integer.valueOf(cap)))
                hasValCap = true;
        }
        Assert.assertTrue(hasValCap);

    }

    @Ignore // this test flickers in @ParallelRunner
    public void testTimeOffer() throws InterruptedException {

        final int cap = 16;
        final BlockingQueue<Integer> dbq = new PushPullBlockingQueue<Integer>(cap);

        for(int i=0; i<cap; i++) {
            dbq.offer(Integer.valueOf(i));
        }

        new Thread(){
            @Override
            public void run() {
                try {
                    sleep(1000);
                    // after a second remove one
                    dbq.poll();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();



        // expect to fail for only 50 ms
        Assert.assertFalse(dbq.offer(Integer.valueOf(cap), 50, TimeUnit.MILLISECONDS));

        Assert.assertTrue(dbq.offer(Integer.valueOf(cap), 1550, TimeUnit.MILLISECONDS));

        boolean hasValCap = false;
        while(!dbq.isEmpty()) {
            if(dbq.poll().equals(Integer.valueOf(cap)))
                hasValCap = true;
        }
        Assert.assertTrue(hasValCap);
    }

    @Test
    public void testTake() throws InterruptedException {

        final int cap = 10;
        final BlockingQueue<Integer> dbq = new PushPullBlockingQueue<Integer>(cap);

        new Thread(){
            @Override
            public void run() {
                try {
                    sleep(1000);
                    // after a second remove one
                    dbq.offer(Integer.valueOf(cap));
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        // wait for value to be added
        Assert.assertEquals(Integer.valueOf(cap), dbq.take());

    }

    @Test
    public void testTimePoll() throws InterruptedException {
        final int cap = 10;
        final BlockingQueue<Integer> dbq = new PushPullBlockingQueue<Integer>(cap);

        new Thread(){
            @Override
            public void run() {
                try {
                    sleep(500);
                    // after a second remove one
                    dbq.offer(Integer.valueOf(cap));
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        // wait for value to be added
        Assert.assertNull(dbq.poll(50, TimeUnit.MICROSECONDS));
        Assert.assertEquals(Integer.valueOf(cap),dbq.poll(50, TimeUnit.SECONDS) );
    }

    @Test
    public void testRemainingCapacity() {
        final int cap = 128;
        final BlockingQueue<Integer> dbq = new PushPullBlockingQueue<Integer>(cap);

        for(int i=0; i<cap; i++) {
            Assert.assertEquals(cap-i, dbq.remainingCapacity());
            dbq.offer(Integer.valueOf(i));
        }

    }

    @Test
    public void testDrainToC() {
        final int cap = 100;
        final BlockingQueue<Integer> dbq = new PushPullBlockingQueue<Integer>(cap);

        for(int i=0; i<cap; i++) {
            dbq.offer(Integer.valueOf(i));
        }

        final List<Integer> c= new LinkedList();
        Assert.assertEquals(dbq.drainTo(c), cap);
        int i=0;
        for(final Integer a : c) {
            Assert.assertEquals(a, Integer.valueOf(i++));
        }


    }

    @Test
    public void drainToToCMax() {

        final int cap = 100;
        final int max = 75;
        final BlockingQueue<Integer> dbq = new PushPullBlockingQueue<Integer>(cap);

        for(int i=0; i<cap; i++) {
            dbq.offer(Integer.valueOf(i));
        }

        final List<Integer> c= new LinkedList();
        Assert.assertEquals(dbq.drainTo(c, max), max);
        Assert.assertEquals(c.size(), max);
        int i=0;
        for(final Integer a : c) {
            Assert.assertEquals(a, Integer.valueOf(i++));
        }
    }

    @Test
    public void testSize() {
        final int cap = 100;
        final BlockingQueue<Integer> dbq = new PushPullBlockingQueue<Integer>(cap);

        Assert.assertEquals(0, dbq.size());

        for(int i=0; i<cap; i++) {
            dbq.offer(Integer.valueOf(i));
            Assert.assertEquals(i+1, dbq.size());
        }

        Assert.assertEquals(cap, dbq.size());

        for(int i=0; i<cap; i++) {
            Assert.assertEquals(dbq.size(), cap-i);
            dbq.poll();
        }

        Assert.assertEquals(0, dbq.size());

        for(int i=0; i<cap; i++) {
            dbq.offer(Integer.valueOf(i));
            Assert.assertEquals(i+1, dbq.size());
        }

        Assert.assertEquals(cap, dbq.size());

        for(int i=0; i<cap; i++) {
            Assert.assertEquals(dbq.size(), cap-i);
            dbq.poll();
        }

    }

    @Test
    public void testIsEmpty() {
        final int cap = 100;
        final BlockingQueue<Integer> dbq = new PushPullBlockingQueue<Integer>(cap);

        Assert.assertTrue(dbq.isEmpty());

        for(int i=0; i<cap; i++) {
            dbq.offer(Integer.valueOf(i));
            Assert.assertFalse(dbq.isEmpty());
        }

        for(int i=0; i<cap; i++) {
            Assert.assertFalse(dbq.isEmpty());
            dbq.poll();
        }

        Assert.assertTrue(dbq.isEmpty());

        for(int i=0; i<cap; i++) {
            dbq.offer(Integer.valueOf(i));
            Assert.assertFalse(dbq.isEmpty());
        }

        for(int i=0; i<cap; i++) {
            Assert.assertFalse(dbq.isEmpty());
            dbq.poll();
        }

        Assert.assertTrue(dbq.isEmpty());
    }

    @Test
    public void testContains() {

        final int cap = 100;
        final BlockingQueue<Integer> dbq = new PushPullBlockingQueue<Integer>(cap);

        for(int i=0; i<cap; i++) {
            Assert.assertFalse(dbq.contains(Integer.valueOf(i)));
        }


        for(int i=0; i<cap; i++) {
            dbq.offer(Integer.valueOf(i));
        }

        for(int i=0; i<cap; i++) {
            Assert.assertTrue(dbq.contains(Integer.valueOf(i)));
        }


        for(int i=cap; i<2*cap; i++) {
            Assert.assertFalse(dbq.contains(Integer.valueOf(i)));
        }
    }

    @Test
    public void testToArray() {

        final int cap = 100;
        final BlockingQueue<Integer> dbq = new PushPullBlockingQueue<Integer>(cap);

        for(int i=0; i<cap; i++) {

            dbq.offer(Integer.valueOf(i));
        }

        Object[] objArray = dbq.toArray();
        for(int i=0; i<cap; i++) {
            Assert.assertEquals(objArray[i], Integer.valueOf(i));
        }

    }

    @Test
    public void testAdd() {
        final int cap = 16;
        final BlockingQueue<Integer> dbq = new PushPullBlockingQueue<Integer>(cap);

        for(int i=0; i<cap; i++) {

            dbq.add(Integer.valueOf(i));
        }

        try {
            dbq.add(Integer.valueOf(cap));
            Assert.fail();
        } catch(IllegalStateException ex) {
            // expected;
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemoveObj() {
        final int cap = 100;
        final BlockingQueue<Integer> dbq = new PushPullBlockingQueue<Integer>(cap);

        for(int i=0; i<cap; i++) {

            dbq.offer(Integer.valueOf(i));
        }

        for(int i=0; i<cap; i+=2) {
            dbq.remove(Integer.valueOf(i));
        }

        Assert.assertEquals(dbq.size(), cap/2);

        for(int i=1; i<cap; i+=2) {
            Assert.assertEquals(Integer.valueOf(i), dbq.poll());
        }
    }


    @Test(expected = UnsupportedOperationException.class)
    public void testRemoveObjDups() {
        final int cap = 100;
        final BlockingQueue<Integer> dbq = new PushPullBlockingQueue<Integer>(cap);

        for(int i=0; i<cap; i++) {
            // all just zeros and ones
            dbq.offer(Integer.valueOf(i&1));
        }

        // nothing removed
        dbq.remove(Integer.valueOf(777));

        Assert.assertEquals(dbq.size(), cap);

        dbq.remove(Integer.valueOf(1));

        Assert.assertEquals(dbq.size(), cap/2);

        for(int i=1; i<cap; i+=2) {
            Assert.assertEquals(Integer.valueOf(0), dbq.poll());
        }
    }


    @Test
    public void testContainsAll() {

        final int cap = 100;
        final BlockingQueue<Integer> dbq = new PushPullBlockingQueue<Integer>(cap);

        for(int i=0; i<cap; i++) {

            dbq.offer(Integer.valueOf(i));
        }

        Set<Integer> si = new HashSet(10);
        for(int i=0; i<cap/10; i++) {
            si.add(Integer.valueOf(i));
        }
        Assert.assertTrue(dbq.containsAll(si));

        si.add(Integer.valueOf(-1));
        Assert.assertFalse(dbq.containsAll(si));
        si.remove(-1);
        dbq.clear();
        Assert.assertFalse(dbq.containsAll(si));
    }

    @Test
    public void testAddAll() {

        final int cap = 100;
        final BlockingQueue<Integer> dbq = new PushPullBlockingQueue<Integer>(cap);

        Set<Integer> si = new HashSet(cap);
        for(int i=0; i<cap/10; i++) {
            si.add(Integer.valueOf(i));
        }
        dbq.addAll(si);
        Assert.assertTrue(dbq.containsAll(si));

        Set<Integer> ni = new HashSet(cap);
        for(int i=0; i<cap/10; i++) {
            ni.add(Integer.valueOf(-i));
        }
        dbq.addAll(ni);
        Assert.assertTrue(dbq.containsAll(si));
        Assert.assertTrue(dbq.containsAll(ni));

        for(int i=2*cap/10; i<2*cap; i++) {
            si.add(Integer.valueOf(i));
        }
        dbq.addAll(si);
        Assert.assertEquals(dbq.size(), 128);
    }

    @Test
    public void testAddAllReturn() {

        final int cap = 8;
        final BlockingQueue<Integer> dbq = new PushPullBlockingQueue<>(cap);

        final Set<Integer> set = new HashSet();

        for(int i=0; i<8; i++) {
            set.add(i);
        }

        Assert.assertTrue(dbq.addAll(set));

        Integer iVal = dbq.poll();
        while(iVal != null) {
            Assert.assertTrue(set.contains(iVal));
            iVal = dbq.poll();
        }

        for(int i=0; i<20; i++) {
            set.add(i);
        }

        // at least one will fail
        Assert.assertTrue(dbq.addAll(set));
    }



    @Test(expected = UnsupportedOperationException.class)
    public void testRemoveAll() {

        final int cap = 100;
        final BlockingQueue<Integer> dbq = new PushPullBlockingQueue<Integer>(cap);

        Set<Integer> si = new HashSet(cap);
        for(int i=0; i<cap/10; i++) {
            si.add(Integer.valueOf(i));
        }
        dbq.addAll(si);
        Assert.assertTrue(dbq.containsAll(si));

        Set<Integer> ni = new HashSet(cap);
        for(int i=1; i<cap/10; i++) {
            ni.add(Integer.valueOf(-i));
        }
        dbq.addAll(ni);
        Assert.assertTrue(dbq.containsAll(si));
        Assert.assertTrue(dbq.containsAll(ni));


        dbq.removeAll(si);
        Assert.assertTrue(dbq.containsAll(ni));
        Assert.assertFalse(dbq.containsAll(si));

        dbq.removeAll(ni);
        Assert.assertFalse(dbq.containsAll(ni));
        Assert.assertFalse(dbq.containsAll(si));
    }

    @Test
    public void testRemoveAll_with_empty_Collection_returns_false_with_no_exception() {

        final int cap = 8;
        final BlockingQueue<Integer> dbq = new PushPullBlockingQueue<>(cap);

        final Set<Integer> set = new HashSet();

        for(int i=0; i<cap; i++) {
            set.add(i);
        }

        dbq.addAll(set);

        Assert.assertFalse(dbq.removeAll(Collections.emptySet()));
        Assert.assertEquals(cap, dbq.size());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRetainAll() {

        final int cap = 100;
        final BlockingQueue<Integer> dbq = new PushPullBlockingQueue<Integer>(cap);

        for(int i=0; i<cap; i++) {
            dbq.offer(Integer.valueOf(i));
        }

        Set<Integer> si = new HashSet(cap);
        for(int i=0; i<cap/10; i++) {
            si.add(Integer.valueOf(i));
        }

        dbq.retainAll(si);

        Assert.assertEquals(cap/10, dbq.size());

        dbq.containsAll(si);
    }

    @Test
    public void testRetainAll_with_equal_Collection_returns_false_with_no_exception() {
        final int cap = 100;
        final BlockingQueue<Integer> dbq = new PushPullBlockingQueue<Integer>(cap);
        Set<Integer> si = new HashSet(cap);

        for(int i=0; i<cap; i++) {
            si.add(Integer.valueOf(i));
            dbq.offer(Integer.valueOf(i));
        }

        Assert.assertFalse(dbq.retainAll(si));
        Assert.assertEquals(cap, dbq.size());
    }

    @Test(timeout=30000)
    public void testOverflowingOffers() throws InterruptedException {
        // there is a problem in the native implementation
        // of null overwriting the added value on the last bucket
        // when the queue was just full
        final int NRUN = 10_000;
        final BlockingQueue<Integer> dbq = new PushPullBlockingQueue<Integer>(64);
        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<NRUN; i++) {
                    while(!dbq.offer(i)) {
                        Thread.yield(); // fast
                    }
                }
            }
        });

        t.start();

        int i;
        for(i=0; i<NRUN; i++) {
            Integer j=null;
            while(j==null) {
                try {
                    Thread.sleep(1); // slower
                } catch(InterruptedException e) {
                    // ignore!
                }
                j = dbq.poll();
            }
        }

        t.join();

        Assert.assertEquals(NRUN, i);
    }

    @Test
    public void testClear() {
        final int cap = 100;
        final BlockingQueue<Integer> dbq = new PushPullBlockingQueue<Integer>(cap);

        for(int i=0; i<cap; i++) {
            dbq.offer(Integer.valueOf(i));
        }

        Set<Integer> si = new HashSet(cap);
        for(int i=0; i<cap/10; i++) {
            si.add(Integer.valueOf(i));
        }

        Assert.assertTrue(dbq.containsAll(si));
        dbq.clear();
        Assert.assertFalse(dbq.containsAll(si));
        Assert.assertEquals(0, dbq.size());
        Assert.assertTrue(dbq.isEmpty());
        Assert.assertNull(dbq.poll());
    }

    @Test
    public void testIterator() {
        final int cap = 100;
        final BlockingQueue<Integer> dbq = new PushPullBlockingQueue<Integer>(cap);

        for(int i=0; i<cap; i++) {
            dbq.offer(Integer.valueOf(i));
        }

        int i=0;
        for(final Integer c : dbq) {
            Assert.assertEquals(Integer.valueOf(i++), c);
        }
    }

    @Test
    public void testTypeToArray() {
        final int cap = 100;
        final BlockingQueue<Integer> dbq = new PushPullBlockingQueue<Integer>(cap);

        for(int i=0; i<cap; i++) {
            dbq.offer(Integer.valueOf(i));
        }

        Integer[] t = new Integer[cap];
        dbq.toArray(t);
        for(int i=0; i<cap; i++) {
            Assert.assertEquals(Integer.valueOf(i), t[i]);
        }
    }


    @Test
    public void textIntMaxValue() {

        // the blocking queue depends on sequence numbers that are integers
        // be sure the blocking queue operates normally over
        // a range spanning integer values

        if(ALLOW_LONG_RUN) {
            final int cap = 3;
            final BlockingQueue<Integer> dbq = new PushPullBlockingQueue<Integer>(cap);

            long  nIter = 0;

            for(int i=0; i<Integer.MAX_VALUE; i++) {

                for(int a=0; a<cap; a++) {
                    Assert.assertEquals(dbq.size(), a);
                    dbq.offer(Integer.valueOf(a));
                    nIter++;
                }

                for(int a=0; a<cap; a++) {
                    Assert.assertEquals(dbq.size(), cap-a);
                    Assert.assertEquals("At i="+i, dbq.poll(),Integer.valueOf(a));
                }

                if(nIter % Integer.MAX_VALUE == 0) System.out.println(nIter+"times MAX_VALUE");

            }
        } else {
            System.out.println("max value test not executed");
        }
    }

    @Test
    public void testSequentialFeed() throws InterruptedException {

        final int feedCount = 2*8192;
        final BlockingQueue<Integer> dbq = new PushPullBlockingQueue<Integer>(128);
        final AtomicInteger nFed = new AtomicInteger(0);
        final AtomicInteger nRead = new AtomicInteger(0);


        final int nFeeders = 1;

        final Thread[] f=new Thread[nFeeders];
        for(int i=0; i<nFeeders; i++) {
            f[i] = new Thread(){
                @Override
                public void run() {
                    try {
                        for(int i = 0; i<feedCount/nFeeders; i++) {
                            while(!dbq.offer(i, 50L, TimeUnit.MICROSECONDS)) yield();
                            nFed.incrementAndGet();
                        }
                    } catch(InterruptedException ex) {

                    }
                }
            };
            f[i].start();
        }

        final int nReaders = 1;
        Thread[] t = new Thread[nReaders];
        for(int i=0; i<nReaders; i++) {
            t[i]=new Thread(){
                @Override
                public void run() {
                    try {
                        while(nRead.get()<feedCount) {
                            Integer r;
                            do {
                                r = dbq.poll(50, TimeUnit.MILLISECONDS);
                                if(r == null) yield();
                            } while((r == null) && (nRead.get()<feedCount));
                            if(r != null) {
                                // we can't control which thread will return
                                // first, but the expected still must be within
                                // the number of threads range
                                Assert.assertTrue(r.intValue()<=nRead.get()+nReaders+1);
                                nRead.incrementAndGet();
                            }
                        }
                    } catch(InterruptedException ex) {

                    }
                }
            };
            t[i].start();
        }
        for(int i=0;i<nFeeders;i++) {
            f[i].join();
        }
        for(int i=0;i<nReaders;i++) {
            t[i].join();
        }

        Assert.assertEquals(nFed.get(), nRead.get());


    }
}
