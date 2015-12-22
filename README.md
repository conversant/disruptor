<img src="https://github.com/conversant/disruptor/blob/master/src/main/resources/ConversantDisruptor.png?raw=true">

# Conversant ConcurrentQueue and Disruptor BlockingQueue

The Disruptor is the highest performing intra-thread transfer mechanism available in Java.  Conversant Disruptor is the highest performing implementation of a ring buffer queue strategy because it has almost no overhead and it exploits a particularly simple design. 

The Conversant Disruptor is designed to be fast by exploiting only just enough optimization while keeping the overall approach to thread transfers direct and simple.   The main advantage of the Conversant Disruptor over other Disruptors is that it is based on the Java BlockingQueue interface so existing software can be immediately adapted to use this disruptor without significant programming changes.

Conversant Disruptor is a drop in replacement for ArrayBlockingQueue with an order of magnitude better performance.  In comparison with LinkedTransferQueue this disruptor implementation is roughly two times faster and does not allocate any objects internally.   Given that LinkedTransferQueue is state of the art in Java performance, Conversant Disruptor may yield significant performance improvements for applications that rely on fast intra-thread transfers.

Conversant Disruptor is capable of 4ns intra-thread transfers for push-pull senarios and 20ns multi-thread transfers in an 1 to N senario.    The "Disruptor" approach is sensitive to huge numbers of threads and will not exibit good performance if your application exploits hundreds or thousands of waiting threads.  Waiting threads will spin-lock the CPU.   From a performance perspective, designing applications to exploit hundreds or thousands of waiting threads is never advisable.
