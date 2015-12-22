package com.conversant.util.concurrent;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Martin Thompson's approach to avoid false cache line sharing
 *
 * Created by jcairns on 5/28/14.
 */
final class PaddedAtomicInteger extends AtomicInteger {
    private volatile int p0;
    private volatile long p1, p2, p3, p4, p5, p6;

    public PaddedAtomicInteger(final int init) {
        super(init);
    }

    public long sumToAvoidOptimization() {
        return p0+p1+p2+p3+p4+p5+p6;
    }

}
