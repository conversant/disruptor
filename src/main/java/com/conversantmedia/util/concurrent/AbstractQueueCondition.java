package com.conversantmedia.util.concurrent;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by jcairns on 12/11/14.
 */

// use java sync to signal
abstract class AbstractQueueCondition implements QueueCondition {

    private final ReentrantLock queueLock = new ReentrantLock();
    private final Condition queueCondition = queueLock.newCondition();

    // wake me when the condition is satisfied, or timeout
    @Override
    public void awaitNanos(final long timeout) throws InterruptedException {
        queueLock.lock();
        try {
            queueCondition.awaitNanos(timeout);
        }
        finally {
            queueLock.unlock();
        }
    }

    // wake if signal is called, or wait indefinitely
    @Override
    public void await() throws InterruptedException {
        queueLock.lock();
        try {
            queueCondition.await();
        }
        finally {
            queueLock.unlock();
        }
    }

    // tell threads waiting on condition to wake up
    @Override
    public void signal() {
        queueLock.lock();
        try {
            queueCondition.signalAll();
        }
        finally {
            queueLock.unlock();
        }

    }

}
