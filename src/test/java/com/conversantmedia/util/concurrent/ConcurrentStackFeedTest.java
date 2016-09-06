package com.conversantmedia.util.concurrent;

import com.conversantmedia.util.collection.Stack;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by john on 2/8/16.
 */
public class ConcurrentStackFeedTest {
    private static final int NTHREAD = Runtime.getRuntime().availableProcessors();
    private static final int NMANY = 1000;

    @Test
    public void measureProgress() {

        final Stack<Integer> iStack = new ConcurrentStack<>(1024);

        final int totes = NTHREAD*NMANY;

        final AtomicInteger count = new AtomicInteger();

        final boolean[] flagSet = new boolean[totes];

        for(int i=0; i<NTHREAD; i++) {
            final int threadOff = i*NMANY;
            final Thread feedThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for(int j = 0; j<NMANY; j++) {
                        while(!iStack.push(threadOff+j)) {
                            Thread.yield();
                        }
                    }
                }
            });
            feedThread.start();

            final Thread getThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    while (count.get() < totes) {
                        final Integer v = iStack.pop();
                        if (v == null) {
                            Thread.yield();
                        } else {
                            flagSet[v.intValue()] = true;
                            count.getAndIncrement();
                        }
                    }
                }
            });

            getThread.start();
        }

        while(count.get() < totes) {
            Thread.yield();
        }

        for(final boolean b : flagSet) {
            Assert.assertTrue("ConcurrentStack did not progress.", b);
        }
    }
}
