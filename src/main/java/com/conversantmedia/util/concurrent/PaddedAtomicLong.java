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

    public String toString() {
        return Long.toString(get());
    }

}
