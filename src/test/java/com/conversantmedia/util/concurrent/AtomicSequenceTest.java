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
