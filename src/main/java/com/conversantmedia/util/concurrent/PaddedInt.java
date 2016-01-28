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
