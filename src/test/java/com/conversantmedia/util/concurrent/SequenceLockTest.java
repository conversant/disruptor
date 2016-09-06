package com.conversantmedia.util.concurrent;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jcairns on 2/12/16.
 */
public class SequenceLockTest {
    private volatile int lastVal = -1;
    private volatile int testVal = 0;

    @Test
    public void testReadLock() {
        final SequenceLock lock = new SequenceLock();

        final long readLock = lock.readLock();

        Assert.assertTrue(readLock>0L);
        Assert.assertTrue(lock.readLockHeld(readLock));

        Assert.assertTrue(lock.tryWriteLock()>0);
    }

    @Test
    public void testWriteLock() {
        final SequenceLock lock = new SequenceLock();
        final long writeLock = lock.writeLock();
        final long readLock = lock.readLock();
        Assert.assertFalse(lock.readLockHeld(readLock));
        lock.unlock(writeLock);
        final long readLock2 = lock.readLock();
        Assert.assertTrue(lock.readLockHeld(readLock2));
        Assert.assertFalse(lock.readLockHeld(readLock));
    }

    @Test
    public void testWriteLockStepsOnRead() {
        final SequenceLock lock = new SequenceLock();
        final long readLock = lock.readLock();
        Assert.assertTrue(lock.readLockHeld(readLock));
        final long writeLock = lock.writeLock();
        Assert.assertFalse(lock.readLockHeld(readLock));
        Assert.assertTrue(writeLock>0L);
        lock.unlock(writeLock);

        final long reader = lock.readLock();
        Assert.assertTrue(lock.readLockHeld(reader));
    }

    @Test
    public void testThreadContention() {
        final int max = 10;
        final int nThread = Runtime.getRuntime().availableProcessors();
        final ExecutorService executor = Executors.newFixedThreadPool(nThread);
        final SequenceLock lock = new SequenceLock();
        for(int i=0; i<nThread; i++) {
            executor.execute(new Runnable() {
                                 @Override
                                 public void run() {

                                     while (testVal < max) {
                                         final long writeLock = lock.tryWriteLock();
                                         if (writeLock > 0) {
                                             Assert.assertEquals(lastVal + 1, testVal);
                                             lastVal = testVal;
                                             testVal = testVal + 1;
                                             lock.unlock(writeLock);
                                         } else {
                                             int lv, tv;
                                             final long readLock = lock.readLock();
                                             lv = lastVal;
                                             tv = testVal;
                                             if (lock.readLockHeld(readLock)) {
                                                 Assert.assertEquals(lv + 1, tv);
                                             }
                                         }
                                     }
                                 }
                             }
            );
        }

        while(testVal < max) {
            Thread.yield();
        }

        final long wLock = lock.writeLock();
        Assert.assertEquals(lastVal+1, testVal);
        lock.unlock(wLock);
        executor.shutdownNow();
    }
}
