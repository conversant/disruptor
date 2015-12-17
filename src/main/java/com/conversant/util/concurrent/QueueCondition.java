package com.conversant.util.concurrent;

/*
 * Return true once a condition is satisfied
 * Created by jcairns on 12/11/14.
 */
interface QueueCondition {

    long PARK_TIMEOUT = 50L;


    // return true if the queue condition is satisfied
    boolean test();

    // wake me when the condition is satisfied, or timeout
    void awaitNanos(final long timeout) throws InterruptedException;

    // wake if signal is called, or wait indefinitely
    void await() throws InterruptedException;

    // tell threads waiting on condition to wake up
    void signal();

}
