package com.conversant.util.concurrent;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Tuned version of Martin Thompson's push pull queue
 *
 * Transfers from a single thread writer to a single thread reader are orders of nanoseconds (3-5)
 *
 * Created by jcairns on 5/28/14.
 */
public class PushPullConcurrentQueue<E> implements ConcurrentQueue<E> {
    protected final int size;
    protected final long mask;

    protected final E[] buffer;

    protected final AtomicLong tail = new PaddedAtomicLong(0L);
    protected final AtomicLong head = new PaddedAtomicLong(0L);

    protected final PaddedLong tailCache = new PaddedLong();
    protected final PaddedLong headCache = new PaddedLong();

    public PushPullConcurrentQueue(final int size) {
        int rs = 1;
        while(rs < size) rs <<= 1;
        this.size = rs;
        this.mask = rs-1;

        buffer = (E[])new Object[this.size];
    }


    @Override
    public boolean offer(final E e) {
        if(e != null) {
            final long tail = this.tail.get();
            final long queueStart = tail - size;
            if((headCache.value > queueStart) || ((headCache.value = head.get()) > queueStart)) {
                final int dx = (int) (tail & mask);
                buffer[dx] = e;
                this.tail.lazySet(tail+1L);
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
        final long head = this.head.get();
        if((head < tailCache.value) || (head < (tailCache.value = tail.get()))) {
            final int dx = (int)(head & mask);
            final E e = buffer[dx];
            buffer[dx] = null;

            this.head.lazySet(head+1L);
            return e;
        } else {
            return null;
        }
    }

    @Override
    public int remove(final E[] e) {
        int n = 0;

        headCache.value = this.head.get();

        final int nMax = e.length;
        for(long i = headCache.value, end = tail.get(); n<nMax && i<end; i++) {
            final int dx = (int) (i & mask);
            e[n++] = buffer[dx];
            buffer[dx] = null;
        }

        this.head.lazySet(headCache.value+n);

        return n;
    }

    @Override
    public void clear() {
        for(int i=0; i<buffer.length; i++) {
            buffer[i] = null;
        }
        head.lazySet(tail.get());
    }


    @Override
    public final E peek() {
        return buffer[(int)(head.get() & mask)];
    }

    @Override
    public final int size() {
        return (int)(tail.get() - head.get());
    }

    @Override
    public int capacity() {
        return size;
    }

    @Override
    public final boolean isEmpty() {
        return tail.get() == head.get();
    }

    @Override
    public final boolean contains(Object o) {
        if(o != null) {
            for(long i = head.get(), end = tail.get(); i<end; i++) {
                final E e = buffer[(int)(i & mask)];
                if(o.equals(e)) {
                    return true;
                }
            }
        }
        return false;
    }
}
