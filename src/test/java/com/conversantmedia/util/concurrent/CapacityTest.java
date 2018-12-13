package com.conversantmedia.util.concurrent;

/*
 * #%L
 * Conversant Disruptor
 * ~~
 * Conversantmedia.com © 2018, Conversant, Inc. Conversant® is a trademark of Conversant, Inc.
 * John Cairns © 2018
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by jcairns on 7/12/2018
 */
public class CapacityTest {

    @Test
    public void testOne() {

        Assert.assertEquals(1, Capacity.getCapacity(1));
    }
    
    @Test
    public void testThree() {

        Assert.assertEquals(4, Capacity.getCapacity(3));
    }
    
    @Test
    public void testIntMax() {

        Assert.assertEquals(Capacity.MAX_POWER2, Capacity.getCapacity(Integer.MAX_VALUE));
    }


    @Test
    public void testIntMax2() {

        Assert.assertEquals(Capacity.MAX_POWER2, Capacity.getCapacity(Integer.MAX_VALUE/2));
    }

    @Test
    public void testIntMax2Plus1() {

        Assert.assertEquals(Capacity.MAX_POWER2, Capacity.getCapacity(Integer.MAX_VALUE/2+1));
    }
}
