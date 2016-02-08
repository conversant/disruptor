package com.conversantmedia.util.collection;

/**
 * A very high performance stack to replace java.util.Stack.  This
 * stack wraps around rather than checking for bounds.
 *
 * The java version of Stack is based on Vector and completely outdated.
 * The performance of java.util.Stack is poor at best.
 *
 * This version is a small fast fixed size stack.   There
 * is no bounds checking so it should only be used when the stack size is known
 * in advance.
 *
 * This object is not thread safe.
 *
 * @author John Cairns <jcairns@dotomi.com> Date: 7/9/12 Time: 8:53 AM
 */
public class FixedStack<N> implements Stack<N> {
	// implement a ring buffer to make sure that it is always safe to push an object into the stack,
	// if stack size is exceeded, the eldest objects are overwritten
	private final int size;
	private final int mask;
	private final N[] stack;
	// use a ring buffer to avoid object manipulation on the stack
	private int   stackTop;

	/**
	 *	 construct a new stack of given capacity
	 */
	public FixedStack(final int size) {
		int stackSize = 1;
		while(stackSize < size) stackSize <<=1;
		this.size = stackSize;
		this.mask = this.size-1;
		stack = (N[])new Object[stackSize];
		stackTop=0;
	}

	/**
	 *	add a node to the stack
	 *
	 * @param n
	 */
	@Override
	public boolean push(final N n) {
		if(stackTop < size) {
			stack[(stackTop++) & mask] = n;
			return true;
		}
		return false;

	}

    @Override
    public boolean contains(final N n) {
        for(int i=0; i<stackTop; i++) {
            if(stack[i].equals(n))
                return true;
        }
        return false;
    }

	/**
	 *	peek at the top of the stack
	 *
	 * @return N - the object at the top of the stack
	 */
	@Override
	public N peek() {
		return stack[(stackTop-1)&mask];
	}

	/**
	 * pop the next element off the stack
	 * @return N - The object on the top of the stack
	 */
	@Override
	public N pop() {
		try {
			return stack[(--stackTop)&mask];
		} finally {
			// remove the reference to the element in the
			// stack to prevent hanging references from living forever
			stack[(stackTop&mask)] = null;
		}
	}

	// return the size of the stack

	/**
	 * Return the size of the stack
	 * @return int - number of elements in the stack
	 */
	@Override
	public int size() {
		return stackTop;
	}

	/**
	 * how much available space in the stack
	 */
	@Override
	public int remainingCapacity() {
		return size - stackTop;
	}

	/**
	 *
	 * @return boolean - true if stack is currently empty
	 */
	@Override
	public boolean isEmpty() {
		return stackTop==0;
	}

	/**
	 *  clear the stack
	 */
	@Override
    public void clear() {
		stackTop=0;
	}

}
