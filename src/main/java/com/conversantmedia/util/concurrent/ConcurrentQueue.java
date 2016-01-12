package com.conversantmedia.util.concurrent;

/**
 * A very high performance blocking buffer, based on Disruptor approach to queues
 *
 * Created by jcairns on 5/28/14.
 */
public interface ConcurrentQueue<E> {

    /**
     * Add element t to the ring
     * @param e
     */
    boolean offer(E e);

    /**
     * remove the first element from the queue and return it
     *
     * @return T
     */
    E poll();

    /**
     * return the first element in the queue
     *
     * @return T
     */
    E peek();

    /**
     * return the number of elements in the queue
     */
    int size();

    /**
     * return the capacity of the queue
     */
    int capacity();

    /**
     * return true if the queue is currently empty
     */
    boolean isEmpty();

    /**
     * return true if specified object is contained in the queue
     */
    boolean contains(Object o);

    /**
     * return all elements in the queue to the provided array, up to the size of the provided
     * array.
     *
     * @param e
     *
     * @return int - the number of elements added to t
     */
    int remove(E[] e);

    /**
     * clear the queue of all elements
     */
    void clear();
}
