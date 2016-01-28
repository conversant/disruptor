package com.conversantmedia.util.concurrent;


import com.conversantmedia.util.collection.Stack;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Concurrent "lock-free" version of a stack.
 *
 * @author John Cairns
 * <p>Date: 7/9/12</p>
 */
public class ConcurrentStack<N> implements Stack<N> {

	private final int size;

	private final N[]  stack;

	// representing the top of the stack
	private final AtomicInteger stackTop = new PaddedAtomicInteger(0);
	// and its cursor - to ensure exclusive access
	private final AtomicInteger stackCursor = new PaddedAtomicInteger(0);

	/**
	 *	 construct a new stack of given capacity
	 *
	 *	 @param size - the stack size
	 */
	public ConcurrentStack(final int size) {
		int stackSize = 1;
		while(stackSize < size) stackSize <<=1;
		this.size = stackSize;
		stack = (N[])new Object[stackSize];
	}

	/**
	 *	add a node to the stack, blocking if necessary until space is
	 *  available
	 *
	 * @param n
	 */
	@Override
	public void push(final N n) {
		while(!add(n)) {
			Thread.yield();
		}
	}

    @Override
    public boolean contains(final N n) {
	    if(n != null) {
		    for(int i = 0; i<stackTop.get(); i++) {
			    if(n.equals(stack[i])) return true;
		    }
	    }
	    return false;
    }

    /**
	 * add an element to the stack, failing if the stack is unable to grow
	 *
	 * @param n
	 *
	 * @return boolean - false if stack overflow, true otherwise
	 */
	@Override
	public boolean add(final N n) {
		for(;;) {
			final int stackTop = this.stackTop.get();
            if (size > stackTop) {
	            final int nextTop = stackTop+1;
	            if(stackCursor.compareAndSet(stackTop, nextTop)) {
		            // this value of stackTop is a constant because we have just locked out changes
		            try {
			            // sequence is still as we expect, not modified elsewhere
			            stack[stackTop] = n;
			            return true;
		            } finally {
			            this.stackTop.lazySet(nextTop);
		            }
	            }
            } else {
	            return false;
            }
			Thread.yield();
		}
	}

	/**
	 *	peek at the top of the stack
	 *
	 * @return N - the object at the top of the stack
	 */
	@Override
	public N peek() {
		// read the current cursor
		final int stackTop = this.stackTop.get();
		if(stackTop > 0) {
			return stack[stackTop-1];
		} else {
			return null;
		}
	}

	/**
	 * pop the next element off the stack
	 * @return N - The object on the top of the stack
	 */
	@Override
	public N pop() {

		// now pop the stack
		for(;;) {
			final int stackTop = this.stackTop.get();
            if(stackTop > 0) {
	            final int lastRef = stackTop - 1;
	            if(stackCursor.compareAndSet(stackTop, lastRef)) {
		            try {
						// if we can modify the stack - i.e. nobody else is modifying
                        final N n = stack[lastRef];
                        stack[lastRef] = null;
                        return n;
					} finally {
			            this.stackTop.lazySet(lastRef);
					}
				}
			} else {
				return null;
			}

			Thread.yield();
		}
	}

	/**
	 * Return the size of the stack
	 * @return int - number of elements in the stack
	 */
	@Override
	public int size() {
		return stackTop.get();
	}

	/**
	 * how much available space in the stack
	 */
	@Override
	public int remainingCapacity() {
		return size - stackTop.get();
	}

	/**
	 * @return boolean - true if stack is currently empty
	 */
	@Override
	public boolean isEmpty() {
		return stackTop.get()==0;
	}

	/**
	 *  clear the stack - does not null old references
	 */
	@Override
	public void clear() {
		for(;;) {
			final int stackTop = this.stackTop.get();
			if(stackTop > 0) {
				if(stackCursor.compareAndSet(stackTop, 0)) {
					try {
						for(int i = 0; i<stackTop; i++) {
							stack[i] = null;
						}
						return;
					} finally {
						this.stackTop.lazySet(0);
					}
				}
			} else {
				return;
			}
			Thread.yield();
		}
	}

	public final static class OverflowException extends Exception {
		private OverflowException() {
			super();
		}
	}
}
