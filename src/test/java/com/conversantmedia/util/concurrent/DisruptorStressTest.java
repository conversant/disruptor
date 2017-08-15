package com.conversantmedia.util.concurrent;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.BitSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by jcairns on 7/31/17.
 */
@Ignore
public class DisruptorStressTest {
    private final static int NTHREAD = Runtime.getRuntime().availableProcessors(); // N read - N write

    private final static int NTOSEND = 10_000_000;

    @Test(timeout=60000L)
    public void disruptorTest() throws InterruptedException {
        feedRequest(new DisruptorBlockingQueue<Integer>(1024));
    }

    @Test(timeout=60000L)
    public void mpmcTest() throws InterruptedException {
        feedRequest(new MPMCBlockingQueue<Integer>(1024));
    }

    @Ignore
    private void feedRequest(final BlockingQueue<Integer> q) throws InterruptedException {

        final LongAdder threadRunning = new LongAdder();

        final Thread[] writeThread = new Thread[NTHREAD];
        final Thread[] readThread  = new Thread[NTHREAD];

        final BitSet totalReq = new BitSet();
        final ReentrantLock totalLock = new ReentrantLock();

        threadRunning.add(NTHREAD);

        for(int i=0; i<NTHREAD; i++) {
            final int threadId = i;
            writeThread[i] = new Thread(() -> {
                try {
                    for (int j = threadId; j < NTOSEND; j += NTHREAD) {
                        while (!q.offer(j)) {
                            Thread.yield();
                        }
                    }
                } finally {
                    threadRunning.decrement();
                }
            }, "write "+i);
            writeThread[i].start();
        }

        for(int i=0; i<NTHREAD; i++) {
            final int threadId = i;
            readThread[i] = new Thread(() -> {

                int nRead = 0;
                final BitSet threadReq = new BitSet();

                while(!q.isEmpty() || threadRunning.sum() > 0) {
                    final Integer m = q.poll();
                    if(m != null) {
                        threadReq.set(m);
                        nRead++;
                    } else {
                        Thread.yield();
                    }

                    if(nRead > 10000) {
                        if(totalLock.tryLock()) {
                            try {
                                totalReq.or(threadReq);
                                threadReq.clear();
                                nRead = 0;
                            } finally {
                                totalLock.unlock();
                            }
                        }
                    }
                }

                totalLock.lock();
                try {
                    totalReq.or(threadReq);
                } finally {
                    totalLock.unlock();
                }
            }, "read "+i);
            readThread[i].start();
        }

        for(int i=0; i<NTHREAD; i++) {
            writeThread[i].join();
            readThread[i].join();
        }

        while(threadRunning.sum() > 0 && !q.isEmpty()) {
            Thread.sleep(250L);
        }

        Assert.assertEquals(NTOSEND, totalReq.cardinality());
        Assert.assertEquals(NTOSEND, totalReq.length());

    }
}
