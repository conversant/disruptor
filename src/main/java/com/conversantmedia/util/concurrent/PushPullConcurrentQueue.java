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

import java.util.concurrent.atomic.LongAdder;

/**
 * Tuned version of Martin Thompson's push pull queue
 *
 * Transfers from a single thread writer to a single thread reader are orders of nanoseconds (3-5)
 *
 * This code is optimized and tested using a 64bit HotSpot JVM on an Intel x86-64 environment.  Other
 * environments should be carefully tested before using in production.
 *
 * Created by jcairns on 5/28/14.
 */

class PushPullConcurrentQueuePadding1 {
    byte p11, p12, p13, p14, p15, p16, p17, p18; // long p1
    byte p21, p22, p23, p24, p25, p26, p27, p28; // long p2
    byte p31, p32, p33, p34, p35, p36, p37, p38; // long p3
    byte p41, p42, p43, p44, p45, p46, p47, p48; // long p4
    byte p51, p52, p53, p54, p55, p56, p57, p58; // long p5
    byte p61, p62, p63, p64, p65, p66, p67, p68; // long p6
    byte p71, p72, p73, p74, p75, p76, p77, p78; // long p7
    byte p81, p82, p83, p84, p85, p86, p87, p88; // long p8
    byte p91, p92, p93, p94, p95, p96, p97, p98; // long p9
    byte p101, p102, p103, p104, p105, p106, p107, p108; // long p10
    byte p111, p112, p113, p114, p115, p116, p117, p118; // long p11
    byte p121, p122, p123, p124, p125, p126, p127, p128; // long p12
    byte p131, p132, p133, p134, p135, p136, p137, p138; // long p13
    byte p141, p142, p143, p144, p145, p146, p147, p148; // long p14
    byte p151, p152, p153, p154, p155, p156, p157, p158; // long p15
}
class PushPullConcurrentQueuePaddingTailCache extends PushPullConcurrentQueuePadding1 {
    long tailCache = 0L;
}
class PushPullConcurrentQueuePadding2 extends PushPullConcurrentQueuePaddingTailCache {
    byte a11, a12, a13, a14, a15, a16, a17, a18; // long a1
    byte a21, a22, a23, a24, a25, a26, a27, a28; // long a2
    byte a31, a32, a33, a34, a35, a36, a37, a38; // long a3
    byte a41, a42, a43, a44, a45, a46, a47, a48; // long a4
    byte a51, a52, a53, a54, a55, a56, a57, a58; // long a5
    byte a61, a62, a63, a64, a65, a66, a67, a68; // long a6
    byte a71, a72, a73, a74, a75, a76, a77, a78; // long a7
    byte a81, a82, a83, a84, a85, a86, a87, a88; // long a8
    byte a91, a92, a93, a94, a95, a96, a97, a98; // long a9
    byte a101, a102, a103, a104, a105, a106, a107, a108; // long a10
    byte a111, a112, a113, a114, a115, a116, a117, a118; // long a11
    byte a121, a122, a123, a124, a125, a126, a127, a128; // long a12
    byte a131, a132, a133, a134, a135, a136, a137, a138; // long a13
    byte a141, a142, a143, a144, a145, a146, a147, a148; // long a14
    byte a151, a152, a153, a154, a155, a156, a157, a158; // long a15
    byte a161, a162, a163, a164, a165, a166, a167, a168; // long a16
}
class PushPullConcurrentQueuePadding3 extends PushPullConcurrentQueuePadding2 {
    byte r11, r12, r13, r14, r15, r16, r17, r18; // long r1
    byte r21, r22, r23, r24, r25, r26, r27, r28; // long r2
    byte r31, r32, r33, r34, r35, r36, r37, r38; // long r3
    byte r41, r42, r43, r44, r45, r46, r47, r48; // long r4
    byte r51, r52, r53, r54, r55, r56, r57, r58; // long r5
    byte r61, r62, r63, r64, r65, r66, r67, r68; // long r6
    byte r71, r72, r73, r74, r75, r76, r77, r78; // long r7
    byte r81, r82, r83, r84, r85, r86, r87, r88; // long r8
    byte r91, r92, r93, r94, r95, r96, r97, r98; // long r9
    byte r101, r102, r103, r104, r105, r106, r107, r108; // long r10
    byte r111, r112, r113, r114, r115, r116, r117, r118; // long r11
    byte r121, r122, r123, r124, r125, r126, r127, r128; // long r12
    byte r131, r132, r133, r134, r135, r136, r137, r138; // long r13
    byte r141, r142, r143, r144, r145, r146, r147, r148; // long r14
    byte r151, r152, r153, r154, r155, r156, r157, r158; // long r15
}
class PushPullConcurrentQueuePaddingHeadCache extends PushPullConcurrentQueuePadding3 {
    long headCache = 0L;
}
class PushPullConcurrentQueuePadding4 extends PushPullConcurrentQueuePaddingHeadCache {
    byte c11, c12, c13, c14, c15, c16, c17, c18; // long c1
    byte c21, c22, c23, c24, c25, c26, c27, c28; // long c2
    byte c31, c32, c33, c34, c35, c36, c37, c38; // long c3
    byte c41, c42, c43, c44, c45, c46, c47, c48; // long c4
    byte c51, c52, c53, c54, c55, c56, c57, c58; // long c5
    byte c61, c62, c63, c64, c65, c66, c67, c68; // long c6
    byte c71, c72, c73, c74, c75, c76, c77, c78; // long c7
    byte c81, c82, c83, c84, c85, c86, c87, c88; // long c8
    byte c91, c92, c93, c94, c95, c96, c97, c98; // long c9
    byte c101, c102, c103, c104, c105, c106, c107, c108; // long c10
    byte c111, c112, c113, c114, c115, c116, c117, c118; // long c11
    byte c121, c122, c123, c124, c125, c126, c127, c128; // long c12
    byte c131, c132, c133, c134, c135, c136, c137, c138; // long c13
    byte c141, c142, c143, c144, c145, c146, c147, c148; // long c14
    byte c151, c152, c153, c154, c155, c156, c157, c158; // long c15
    byte c161, c162, c163, c164, c165, c166, c167, c168; // long c16
}
public class PushPullConcurrentQueue<E> extends PushPullConcurrentQueuePadding4 implements ConcurrentQueue<E> {

    final int size;

    final long mask;

    final LongAdder tail = new LongAdder();

    final E[] buffer;

    final LongAdder head = new LongAdder();

    public PushPullConcurrentQueue(final int capacity) {
        this.size = Capacity.getCapacity(capacity);
        this.mask = this.size-1;

        buffer = (E[])new Object[this.size];
    }


    @Override
    public boolean offer(final E e) {
        if(e != null) {
            final long tail = this.tail.sum();
            final long queueStart = tail - size;
            if((headCache > queueStart) || ((headCache = head.sum()) > queueStart)) {
                final int dx = (int) (tail & mask);
                buffer[dx] = e;
                this.tail.increment();
                return true;
            } else {
                return false;
            }
        } else {
            throw new NullPointerException("Invalid element");
        }
    }

    @Override
    public E poll() {
        final long head = this.head.sum();
        if((head < tailCache) || (head < (tailCache = tail.sum()))) {
            final int dx = (int)(head & mask);
            final E e = buffer[dx];
            buffer[dx] = null;

            this.head.increment();
            return e;
        } else {
            return null;
        }
    }

    @Override
    public int remove(final E[] e) {
        int n = 0;

        headCache = this.head.sum();

        final int nMax = e.length;
        for(long i = headCache, end = tail.sum(); n<nMax && i<end; i++) {
            final int dx = (int) (i & mask);
            e[n++] = buffer[dx];
            buffer[dx] = null;
        }

        this.head.add(n);

        return n;
    }

    @Override
    public void clear() {
        for(int i=0; i<buffer.length; i++) {
            buffer[i] = null;
        }
        head.add(tail.sum()-head.sum());
    }


    @Override
    public final E peek() {
        return buffer[(int)(head.sum() & mask)];
    }

    /**
     * This implemention is known to be broken if preemption were to occur after
     * reading the tail pointer.
     *
     * Code should not depend on size for a correct result.
     *
     * @return int - possibly the size, or possibly any value less than capacity()
     */
    @Override
    public final int size() {
        return (int)Math.max(tail.sum() - head.sum(), 0);
    }

    @Override
    public int capacity() {
        return size;
    }

    @Override
    public final boolean isEmpty() {
        return tail.sum() == head.sum();
    }

    @Override
    public final boolean contains(Object o) {
        if(o != null) {
            for(long i = head.sum(), end = tail.sum(); i<end; i++) {
                final E e = buffer[(int)(i & mask)];
                if(o.equals(e)) {
                    return true;
                }
            }
        }
        return false;
    }

    long sumToAvoidOptimization() {
        return // Padding1 p1-p15
                 p11+p12+p13+p14+p15+p16+p17+p18
                +p21+p22+p23+p24+p25+p26+p27+p28
                +p31+p32+p33+p34+p35+p36+p37+p38
                +p41+p42+p43+p44+p45+p46+p47+p48
                +p51+p52+p53+p54+p55+p56+p57+p58
                +p61+p62+p63+p64+p65+p66+p67+p68
                +p71+p72+p73+p74+p75+p76+p77+p78
                +p81+p82+p83+p84+p85+p86+p87+p88
                +p91+p92+p93+p94+p95+p96+p97+p98
                +p101+p102+p103+p104+p105+p106+p107+p108
                +p111+p112+p113+p114+p115+p116+p117+p118
                +p121+p122+p123+p124+p125+p126+p127+p128
                +p131+p132+p133+p134+p135+p136+p137+p138
                +p141+p142+p143+p144+p145+p146+p147+p148
                +p151+p152+p153+p154+p155+p156+p157+p158
              // Padding2 a1-a16
                +a11+a12+a13+a14+a15+a16+a17+a18
                +a21+a22+a23+a24+a25+a26+a27+a28
                +a31+a32+a33+a34+a35+a36+a37+a38
                +a41+a42+a43+a44+a45+a46+a47+a48
                +a51+a52+a53+a54+a55+a56+a57+a58
                +a61+a62+a63+a64+a65+a66+a67+a68
                +a71+a72+a73+a74+a75+a76+a77+a78
                +a81+a82+a83+a84+a85+a86+a87+a88
                +a91+a92+a93+a94+a95+a96+a97+a98
                +a101+a102+a103+a104+a105+a106+a107+a108
                +a111+a112+a113+a114+a115+a116+a117+a118
                +a121+a122+a123+a124+a125+a126+a127+a128
                +a131+a132+a133+a134+a135+a136+a137+a138
                +a141+a142+a143+a144+a145+a146+a147+a148
                +a151+a152+a153+a154+a155+a156+a157+a158
                +a161+a162+a163+a164+a165+a166+a167+a168
              // Padding3 r1-r15
                +r11+r12+r13+r14+r15+r16+r17+r18
                +r21+r22+r23+r24+r25+r26+r27+r28
                +r31+r32+r33+r34+r35+r36+r37+r38
                +r41+r42+r43+r44+r45+r46+r47+r48
                +r51+r52+r53+r54+r55+r56+r57+r58
                +r61+r62+r63+r64+r65+r66+r67+r68
                +r71+r72+r73+r74+r75+r76+r77+r78
                +r81+r82+r83+r84+r85+r86+r87+r88
                +r91+r92+r93+r94+r95+r96+r97+r98
                +r101+r102+r103+r104+r105+r106+r107+r108
                +r111+r112+r113+r114+r115+r116+r117+r118
                +r121+r122+r123+r124+r125+r126+r127+r128
                +r131+r132+r133+r134+r135+r136+r137+r138
                +r141+r142+r143+r144+r145+r146+r147+r148
                +r151+r152+r153+r154+r155+r156+r157+r158
              // Padding4 c1-c16
                +c11+c12+c13+c14+c15+c16+c17+c18
                +c21+c22+c23+c24+c25+c26+c27+c28
                +c31+c32+c33+c34+c35+c36+c37+c38
                +c41+c42+c43+c44+c45+c46+c47+c48
                +c51+c52+c53+c54+c55+c56+c57+c58
                +c61+c62+c63+c64+c65+c66+c67+c68
                +c71+c72+c73+c74+c75+c76+c77+c78
                +c81+c82+c83+c84+c85+c86+c87+c88
                +c91+c92+c93+c94+c95+c96+c97+c98
                +c101+c102+c103+c104+c105+c106+c107+c108
                +c111+c112+c113+c114+c115+c116+c117+c118
                +c121+c122+c123+c124+c125+c126+c127+c128
                +c131+c132+c133+c134+c135+c136+c137+c138
                +c141+c142+c143+c144+c145+c146+c147+c148
                +c151+c152+c153+c154+c155+c156+c157+c158
                +c161+c162+c163+c164+c165+c166+c167+c168;
    }
}
