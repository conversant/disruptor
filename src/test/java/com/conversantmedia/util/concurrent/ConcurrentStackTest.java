package com.conversantmedia.util.concurrent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    public void add_Pop() throws Exception {
        final ConcurrentStack<Integer> stack = new ConcurrentStack<>(10);

        stack.add(1);
        stack.add(2);
        stack.add(3);

        assertEquals(Integer.valueOf(3), stack.pop());
        assertEquals(Integer.valueOf(2), stack.pop());
        assertEquals(Integer.valueOf(1), stack.pop());
        assertNull(stack.pop());
    }

    @Test(timeout = 10000)
    public void multithreaded_Add_Pop() throws Exception {
        final ConcurrentStack<Integer> stack = new ConcurrentStack<>(10);

        final AtomicInteger addCount = new AtomicInteger(0);
        final AtomicInteger popCount = new AtomicInteger(0);


        for (int i=0; i < 100000; i++) {
            executor.execute(() -> {
                if(stack.pop() != null) {
                    popCount.incrementAndGet();
                }
            });
            executor.execute(() -> stack.add(1));
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

}