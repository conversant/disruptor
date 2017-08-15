package com.conversantmedia.util.concurrent;

import java.util.concurrent.atomic.LongAdder;

/**
 * Created by jcairns on 7/28/17.
 */
final class ContendedLongAdder extends LongAdder {

    long p1, p2, p3, p4, p5, p6, p7;
    long a1, a2, a3, a4, a5, a6, a7, a8;

    public long sumToAvoidOptimization() {
        return p1+p2+p3+p4+p5+p6+p7+a1+a2+a3+a4+a5+a6+a7+a8+sum();
    }

    public String toString() {
        return Long.toString(sum());
    }

}
