package com.conversantmedia.util.collection;

/**
 * Created by jcairns on 6/11/14.
 */
public interface Stack<N> {


	boolean contains(N n);

	/**
	 * Add the element to the stack top, optionally failing if there is
	 * no capacity
	 *
	 * @param n
	 * @return
	 */
	boolean add(N n);

	/**
	 * push the next element on the stack top - waiting if needed for space to become available
	 *
	 * @param n
	 */
	void push(N n);

	/**
	 * show the current stack top
	 * @return
	 */
	N peek();

	/**
	 * pop and return the element from the top of the stack
     *
	 * @return N - the element, or null if the stack is empty
	 */
	N pop();

	/**
	 * return the size of the stack in number of elements
	 *
	 * @return
	 */
	int size();

	/**
	 * return the number of empty slots available in the stack
	 *
	 * @return
	 */
	int remainingCapacity();

	/**
	 * @return boolean - true if stack is empty
	 */
	boolean isEmpty();

	/**
	 * clear the stack
	 */
	void clear();
}
