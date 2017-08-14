package com.conversantmedia.util.concurrent;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by jcairns on 8/14/17.
 */
public class ContendedAtomicLongTest {

    @Test
    public void testInit() {
        Assert.assertEquals(37347, new ContendedAtomicLong(37347L).get());
    }

    @Test
    public void testSet() {
        final ContendedAtomicLong al = new ContendedAtomicLong(0L);
        al.set(676L);
        Assert.assertEquals(676L, al.get());
    }

    @Test
    public void testCAS() {
        final ContendedAtomicLong al = new ContendedAtomicLong(667L);

        if(al.compareAndSet(700L, 800L)) {
            Assert.fail("CAS operation failed.");
        }

        Assert.assertTrue(al.compareAndSet(667L, 777L));
        Assert.assertEquals(777L, al.get());
    }
}
