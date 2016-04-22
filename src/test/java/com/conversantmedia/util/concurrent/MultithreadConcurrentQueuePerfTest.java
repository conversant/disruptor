package com.conversantmedia.util.concurrent;

/*
 * #%L
 * Conversant Disruptor
 * ~~
 * Conversantmedia.com © 2016, Conversant, Inc. Conversant® is a trademark of Conversant, Inc.
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

import org.junit.Ignore;
import org.junit.Test;

import com.conversantmedia.util.estimation.Percentile;

/**
 * Created by jcairns on 5/28/14.
 */
@Ignore
public class MultithreadConcurrentQueuePerfTest {

    @Test
    public void testPerformance() throws InterruptedException, Percentile.InsufficientSamplesException {
        System.out.println("1x1 using MultiThread");
        ConcurrentQueuePerformanceTest.testPerformance(new MultithreadConcurrentQueue<>(1024));
    }


    @Test
    public void testRate() throws InterruptedException, Percentile.InsufficientSamplesException {
        ConcurrentQueuePerformanceTest.testRate(new MultithreadConcurrentQueue<>(1024));
    }

    @Ignore
    public void testNThreadPerformance() throws Percentile.InsufficientSamplesException, InterruptedException {
        System.out.println("NThread in MultiThread");
        ConcurrentQueuePerformanceTest.testNThreadPerformance(new MultithreadConcurrentQueue<>(1024));
    }

    @Test
    public void testNxNThreadPerformance() throws Percentile.InsufficientSamplesException, InterruptedException {
        System.out.println("NThread in MultiThread");
        ConcurrentQueuePerformanceTest.testNxNThreadPerformance(new MultithreadConcurrentQueue<>(1024));
    }

}
