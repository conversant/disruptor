package com.conversantmedia.util.concurrent;

import com.conversantmedia.util.concurrent.ConcurrentStack;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Greg Opaczewski
 */
public class ConcurrentStackTest {

    private static final int N_THREADS = Runtime.getRuntime().availableProcessors();

    private ExecutorService executor;


    @Before
    public void setup() {
        executor = Executors.newFixedThreadPool(N_THREADS);
    }

    @After
    public void teardown() {
        executor.shutdown();
    }

    @Test
    public void peekEmptyTest() {

        final ConcurrentStack<Integer> stack = new ConcurrentStack<>(10);

        Assert.assertNull(stack.peek());
    }

    @Test
    public void peekTopTest() {

        final ConcurrentStack<Integer> stack = new ConcurrentStack<>(10);

        stack.push(100);

        Assert.assertEquals(Integer.valueOf(100), stack.peek());

    }

    @Test
    public void pushPopTest() throws Exception {
        final ConcurrentStack<Integer> stack = new ConcurrentStack<>(10);

        stack.push(1);
        stack.push(2);
        stack.push(3);

        assertEquals(Integer.valueOf(3), stack.pop());
        assertEquals(Integer.valueOf(2), stack.pop());
        assertEquals(Integer.valueOf(1), stack.pop());
        assertNull(stack.pop());
    }

    @Test(timeout = 10000)
    public void multithreadPushPop() throws Exception {
        final ConcurrentStack<Integer> stack = new ConcurrentStack<>(10);

        final AtomicInteger addCount = new AtomicInteger(0);
        final AtomicInteger popCount = new AtomicInteger(0);


        for (int i=0; i < 100000; i++) {
            executor.execute(() -> {
                if(stack.pop() != null) {
                    popCount.incrementAndGet();
                }
            });
            executor.execute(() -> stack.push(1));
        }

        while (popCount.get() < addCount.get()) {
            executor.execute(() -> {
                assertNotNull(stack.pop());
                popCount.incrementAndGet();
            });
        }
    }

    @Test(timeout = 10000)
    public void multithreadTestSequenceTest() {
        final ConcurrentStack<Integer> stack = new ConcurrentStack<>(1024);

        final AtomicInteger nFound = new AtomicInteger();

        for(int i=1; i<=1024; i++) {
            stack.push(1024-i);

        }

        for(int i=0; i<1024; i++) {
            final Integer bottom = i;
            executor.execute(() -> {
                        while(!bottom.equals(stack.peek()))
                            Thread.yield();
                        stack.pop();
                        nFound.incrementAndGet();
                    }
            );
        }

        while(nFound.get() < 1024) Thread.yield();
        assertEquals(1024, nFound.get());
    }


    @Test(timeout=1000L)
    public void timedPushTest() throws InterruptedException {
        final ConcurrentStack<Integer> iStack = new ConcurrentStack<>(128);

        for(int i=0; i<128; i++) {
            iStack.push(i);
        }

        Assert.assertFalse(iStack.push(129, 10L, TimeUnit.MICROSECONDS));
    }

    @Test
    public void pushInterruptably() throws InterruptedException {
        final ConcurrentStack<Integer> iStack = new ConcurrentStack<>(128);
        final AtomicBoolean expectInterrupt = new AtomicBoolean(false);

        for(int i=0; i<128; i++) {
            iStack.pushInterruptibly(i);
        }

        final Thread t = new Thread(() -> {
            try {
                Thread.currentThread().interrupt();
                iStack.pushInterruptibly(129);
            } catch (InterruptedException e) {
                expectInterrupt.set(true);
            }
        });


        t.start();
        t.join();

        Assert.assertEquals(true, expectInterrupt.get());

    }

    @Test
    public void containsTest() {
        final ConcurrentStack<Integer> iStack = new ConcurrentStack<>(128);

        for(int i=0; i<128; i++) {
            iStack.push(i);
        }

        for(int i=0; i<128; i++) {
            Assert.assertTrue(iStack.contains(i));
        }

    }

    @Test
    public void pushOverflowTest() {
        final ConcurrentStack<Integer> iStack = new ConcurrentStack<>(128);

        for(int i=0; i<128; i++) {
            iStack.push(i);
        }

        Assert.assertFalse(iStack.push(129));
    }

    @Test
    public void peekTest() {
        final ConcurrentStack<Integer> iStack = new ConcurrentStack<>(128);
        for(int i=0; i<128; i++) {
            iStack.push(i);
            Assert.assertEquals(Integer.valueOf(i), iStack.peek());
            iStack.pop();
        }
    }

    @Test
    public void popNullTest() {
        final ConcurrentStack<Integer> iStack = new ConcurrentStack<>(128);
        Assert.assertNull(iStack.pop());
    }

    @Test(timeout=1000L)
    public void timedPopNullTest() throws InterruptedException {
        final ConcurrentStack<Integer> iStack = new ConcurrentStack<>(128);
        Assert.assertNull(iStack.pop(10L, TimeUnit.MICROSECONDS));
    }


    @Test
    public void popInterruptably() throws InterruptedException {
        final ConcurrentStack<Integer> iStack = new ConcurrentStack<>(128);
        final AtomicBoolean expectInterrupt = new AtomicBoolean(false);



        final Thread t = new Thread(() -> {
            try {
                Thread.currentThread().interrupt();
                iStack.popInterruptibly();
            } catch (InterruptedException e) {
                expectInterrupt.set(true);
            }
        });


        t.start();
        t.join();

        Assert.assertEquals(true, expectInterrupt.get());
    }

}