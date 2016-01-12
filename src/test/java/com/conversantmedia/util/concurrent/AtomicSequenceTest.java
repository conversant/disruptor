package com.conversantmedia.util.concurrent;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by jcairns on 9/24/14.
 */
public class AtomicSequenceTest {

    volatile long incSeq;

    @Before
    public void setup() {
        incSeq = 0L;
    }


    @Test
    public void testIsAtomic() throws InterruptedException {
        final AtomicSequence x = new AtomicSequence();

        final int NTHREAD = 10;
        final int NLOOP = 1024*1*2;

        final Thread[] thread = new Thread[NTHREAD];
        for(int i=0; i<NTHREAD; i++) {
            thread[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for(int i=0; i<NLOOP; i++) {
                        outerFor:
                        for(;;) {
                            final long s = x.get();
                            if(x.update(s)) {
                                try {
                                    // note this operation is not normally atomic
                                    // but here is must be, because this thread is
                                    // permitted to update the sequence
                                    incSeq = incSeq+1L;
                                } finally {
                                    x.commit();
                                }
                                break outerFor;
                            }
                        }
                    }
                }
            });

            thread[i].start();
        }

        for(int i=0; i<NTHREAD; i++) {
            thread[i].join();
        }

        Assert.assertEquals(NTHREAD*NLOOP, incSeq);

    }
}
