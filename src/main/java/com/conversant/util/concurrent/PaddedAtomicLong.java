package com.conversant.util.concurrent;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Martin Thompson's approach to avoid false cache line sharing
 *
 * Created by jcairns on 5/28/14.
 */
final class PaddedAtomicLong extends AtomicLong {
    private volatile long p1, p2, p3, p4, p5, p6;

    public PaddedAtomicLong(final long init) {
        super(init);
    }

    public long sumToAvoidOptimization() {
        return p1+p2+p3+p4+p5+p6;
    }

}
