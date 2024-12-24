<img src="https://github.com/conversant/disruptor/blob/master/src/main/resources/ConversantDisruptorLogo.png?raw=true">

# Conversant ConcurrentQueue, Disruptor BlockingQueue and ConcurrentStack

Disruptor is the highest performing intra-thread transfer mechanism available in Java.  Conversant Disruptor is the highest performing implementation of this type of ring buffer because it has almost no overhead and it exploits a particularly simple design. 

<table>
<td><img src="https://github.com/conversant/disruptor/blob/master/benchmark/benchmark.jpg?raw=true"></td><tr>
<caption><strong>2017 Conversant Disruptor - Still the World's Fastest</strong></caption>
</table>

# Benchmark First!

Conversant Disruptor was designed to run on Intel Xeon hardware.   For any other platform or 
architecture always benchmark before using. 

g# Getting Started

Run the maven build to build and use the package.

```$ mvn -U clean package```

# Conversant Disruptor is on Maven Central

For Java 9 and above:

```
<dependency>
  <groupId>com.conversantmedia</groupId>
  <artifactId>disruptor</artifactId>
  <version>1.2.16</version>
</dependency>
```

A classifier is not required in the latest release.

Java 8 is only supported in 1.2.15 and earlier.

Java 7 is only supported in 1.2.10 and earlier.   

```
<dependency>
  <groupId>com.conversantmedia</groupId>
  <artifactId>disruptor</artifactId>
  <version>1.2.10</version>
  <classifier>jdk7</classifier>
</dependency>
```

## Discussion Forum

Conversant Disruptor has a google group so you can follow releases and changes:   
https://groups.google.com/forum/#!forum/conversant-disruptor

