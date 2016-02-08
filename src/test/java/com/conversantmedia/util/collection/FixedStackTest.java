package com.conversantmedia.util.collection;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author John Cairns <jcairns@dotomi.com> Date: 7/9/12 Time: 9:02 AM
 */
public class FixedStackTest {

	@Test
	public void testCreate() {
		FixedStack<Integer> intStack = new FixedStack<Integer>(10);

		Assert.assertTrue(intStack.isEmpty());

		for(int i=0; i<10; i++) {
			intStack.push(i);
		}

		Assert.assertFalse(intStack.isEmpty());

		Assert.assertEquals(intStack.size(), 10);

		for(int i=9; i>=0; i--) {
			Assert.assertEquals(intStack.pop(), Integer.valueOf(i));
		}
	}

	@Test
	public void testPush() {
		FixedStack<Integer> intStack = new FixedStack<Integer>(16);
		// okay to add more than 10
		for(int i=0; i<16; i++) {
			intStack.push(i);
		}

		for(int i=0; i<5; i++) {
            Assert.assertFalse(intStack.push(i));
        }

		// but they are not on the stack -
		for(int i=15; i>=0; i--) {
			Assert.assertEquals(Integer.valueOf(i), intStack.pop());
		}
	}

	@Test
	public void testPeek() {
		FixedStack<Integer> intStack = new FixedStack<Integer>(1024);
		for(int i=0; i<1024; i++) {
			intStack.push(i);
		}
		// confirm peek matches what we expect
		for(int i=1023; i>=0; i--) {
			Assert.assertEquals(intStack.peek(), Integer.valueOf(i));
			Assert.assertEquals(intStack.pop(), Integer.valueOf(i));
		}
	}

	@Test
	public void testClear() {
		FixedStack<Integer> intStack = new FixedStack<Integer>(20);
		for(int i=0; i<20; i++) {
			intStack.push(i);
		}

		Assert.assertFalse(intStack.isEmpty());
		intStack.clear();
		Assert.assertTrue(intStack.isEmpty());
		for(int i=0; i<20; i++) {
			intStack.push(i);
		}
		for(int i=19; i>=0; i--) {
			Assert.assertEquals(intStack.pop(), Integer.valueOf(i));
		}
	}
}
