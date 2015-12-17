package com.conversant.util.concurrent;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.LockSupport;

/**
 * Created by jcairns on 12/11/14.
 */
// abstract condition supporting common condition code
abstract class AbstractWaitingQueueCondition implements QueueCondition {


    // keep track of whos waiting so we don't have to synchronize
    // or notify needlessly - when nobody is waiting

    private static final int MAX_WAITERS = 8;
    private static final int MASK_WAITERS = MAX_WAITERS-1;

    private static final long WAIT_TIME = PARK_TIMEOUT;

    private final AtomicReferenceArray<Thread> waiter = new AtomicReferenceArray<Thread>(MAX_WAITERS);

    private final AtomicInteger waitCount = new PaddedAtomicInteger(0);
    private final PaddedInt waitCache = new PaddedInt(0);

    // @return boolean - true if condition is satisfied
    @Override
    public abstract boolean test();

    @Override
    public void awaitNanos(long timeout) throws InterruptedException {
        for (;;) {
            try {
                final int waitCount = this.waitCount.get();
                int waitSequence = waitCount;

                if (this.waitCount.compareAndSet(waitCount, waitCount + 1)) {
                    waitCache.value = waitCount+1;

                    long timeNow = System.nanoTime();
                    long expires = timeNow+timeout;

                    final Thread t = Thread.currentThread();

                    if(waitCount == 0) {
                        // first thread spins
                        while(test() && expires>timeNow && !t.isInterrupted()) {
                            while(test() && expires>timeNow && !t.isInterrupted()) {
                                timeNow += 5L;
                            }
                            Thread.yield();
                            timeNow = System.nanoTime();
                        }

                        if(t.isInterrupted()) {
                            throw new InterruptedException();
                        }

                        return;
                    } else {
                        // wait to become a waiter
                        while(test() && !waiter.compareAndSet(waitSequence++ & MASK_WAITERS, null, t) && expires>timeNow) {
                            // too many threads are waiting?
                            if((waitSequence & MASK_WAITERS) == MASK_WAITERS) {
                                // stall after N tries because every waiting thread slot is already occupied
                                LockSupport.parkNanos(WAIT_TIME*MAX_WAITERS);
                                timeNow = System.nanoTime();
                            }
                        }
                        // are we a waiter?   wait until we are awakened
                        while(test() && (waiter.get((waitSequence-1) & MASK_WAITERS) == t) && expires>timeNow && !t.isInterrupted()) {
                            LockSupport.parkNanos((expires-timeNow)>>2);
                            timeNow = System.nanoTime();
                        }

                        if(t.isInterrupted()) {
                            // we are not waiting we are interrupted
                            while(!waiter.compareAndSet((waitSequence-1) & MASK_WAITERS, t, null) && waiter.get(0) == t) {
                                LockSupport.parkNanos(PARK_TIMEOUT);
                            }

                            throw new InterruptedException();
                        }

                        return;


                    }
                }
            }finally{
                waitCache.value = waitCount.decrementAndGet();
            }
        }
    }

    @Override
    public void await() throws InterruptedException {
        for(;;) {
            try {
                final int waitCount = this.waitCount.get();
                int waitSequence = waitCount;

                if (this.waitCount.compareAndSet(waitCount, waitCount + 1)) {
                    waitCache.value = waitCount+1;

                    final Thread t = Thread.currentThread();

                    if(waitCount == 0) {
                        // first thread spinning
                        while(test() && !t.isInterrupted()) {
                            LockSupport.parkNanos(PARK_TIMEOUT);
                        }

                        if(t.isInterrupted()) {
                            throw new InterruptedException();
                        }

                        return;
                    } else {

                        // wait to become a waiter
                        while(test() && !waiter.compareAndSet(waitSequence++ & MASK_WAITERS, null, t) && !t.isInterrupted()) {
                            if((waitSequence & MASK_WAITERS) == MASK_WAITERS) {
                                // stall after N tries because every waiting thread slot is already occupied
                                LockSupport.parkNanos(MAX_WAITERS*WAIT_TIME);
                            }
                        }

                        // are we a waiter?   wait until we are awakened
                        while(test() && (waiter.get((waitSequence-1) & MASK_WAITERS) == t) && !t.isInterrupted()) {
                            LockSupport.park();
                        }

                        if(t.isInterrupted()) {
                            // we are not waiting we are interrupted
                            while(!waiter.compareAndSet((waitSequence-1) & MASK_WAITERS, t, null) && waiter.get(0) == t) {
                                LockSupport.parkNanos(PARK_TIMEOUT);
                            }

                            throw new InterruptedException();
                        }

                        return;

                    }

                }
            } finally {
                waitCache.value = waitCount.decrementAndGet();
            }
        }
    }

    @Override
    public void signal() {
        // only signal if somebody is blocking for it
        if (waitCache.value > 0 || (waitCache.value = waitCount.get()) > 0) {
            int waitSequence = 0;
            for(;;) {
                Thread t;
                while((t = waiter.get(waitSequence++ & MASK_WAITERS)) != null) {
                    if(waiter.compareAndSet((waitSequence-1) & MASK_WAITERS, t, null)) {
                        LockSupport.unpark(t);
                    } else {
                        LockSupport.parkNanos(PARK_TIMEOUT);
                    }

                    // go through all waiters once, or return if we are finished
                    if(((waitSequence & MASK_WAITERS) == MASK_WAITERS) || (waitCache.value = waitCount.get()) == 0) {
                        return;
                    }
                }

                // go through all waiters once, or return if we are finished
                if(((waitSequence & MASK_WAITERS) == MASK_WAITERS) || (waitCache.value = waitCount.get()) == 0) {
                    return;
                }
            }
        }
    }
}
