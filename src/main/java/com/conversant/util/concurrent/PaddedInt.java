package com.conversant.util.concurrent;

/**
 * Created by jcairns on 5/28/14.
 */
final class PaddedInt {
    public int value=0, p0;
    public long p1, p2, p3, p4, p5, p6;

    public PaddedInt() {

    }

    public PaddedInt(final int c) {
        value=c;
    }

    public long sumToAvoidOptimization() {
        return p0+p1+p2+p3+p4+p5+p6;
    }
}
