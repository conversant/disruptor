package com.conversantmedia.util.concurrent;

import com.conversantmedia.util.collection.Stack;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by jcairns on 1/26/16.
 */
public class ConcurrentStackPerformanceTest {
    private static final int NRUN = 1000;
    private static final Integer INTVAL = 6767;

    @Test
    public void testStackPerformance() throws InterruptedException {
        for(int c=0; c<6; c++) {
            System.gc();
            runRate(new ConcurrentStack<>(1024));
        }
    }

    @Ignore
    private static void runRate(Stack<Integer> stack) throws InterruptedException {
        final Thread thread = new Thread(() -> {
            for(int i=0; i<NRUN; i++) {
                while(!stack.add(INTVAL)) {
                    Thread.yield();;
                }
            }
        });

        final long startTime = System.nanoTime();
        thread.start();
        for(int i=0; i<NRUN; i++) {
            while(stack.pop() != INTVAL) {
                Thread.yield();
            }
        }
        thread.join();
        final long runTime = System.nanoTime() - startTime;
        System.out.println(Integer.toString(NRUN)+" in "+String.format("%3.1f ms", runTime/1e6)+": "+String.format("%d ns", runTime/NRUN));
    }
}
