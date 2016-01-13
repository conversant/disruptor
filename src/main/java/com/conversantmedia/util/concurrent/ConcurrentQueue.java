package com.conversantmedia.util.concurrent;

/**
 * A very high performance blocking buffer, based on Disruptor approach to queues
 *
 * Created by jcairns on 5/28/14.
 */
public interface ConcurrentQueue<E> {

    /**
     * Add element t to the ring
     * @param e - element to offer
     * 
     * @return boolean - true if the operation succeeded
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
     * @return E - The element
     */
    E peek();

    /**
     * @return int - the number of elements in the queue
     */
    int size();

    /**
     * @return int - the capacity of the queue
     */
    int capacity();

    /**
     * @return boolean - true if the queue is currently empty
     */
    boolean isEmpty();

    /**
     * @param o - the object to test
     *
     * @return boolean - true if specified object is contained in the queue
     */
    boolean contains(Object o);

    /**
     * return all elements in the queue to the provided array, up to the size of the provided
     * array.
     *
     * @param e - The element array
     *
     * @return int - the number of elements added to t
     */
    int remove(E[] e);

    /**
     * clear the queue of all elements
     */
    void clear();
}
