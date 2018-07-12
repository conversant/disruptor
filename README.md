<img src="https://github.com/conversant/disruptor/blob/master/src/main/resources/ConversantDisruptorLogo.png?raw=true">

# Conversant ConcurrentQueue, Disruptor BlockingQueue and ConcurrentStack

Disruptor is the highest performing intra-thread transfer mechanism available in Java.  Conversant Disruptor is the highest performing implementation of this type of ring buffer because it has almost no overhead and it exploits a particularly simple design. 

<table>
<td><img src="https://github.com/conversant/disruptor/blob/master/benchmark/benchmark.jpg?raw=true"></td><tr>
<caption><strong>2017 Conversant Disruptor - Still the World's Fastest</strong></caption>
</table>

# Getting Started

Run the maven build to build and use the package.

```$ mvn -U clean package```

# Conversant Disruptor is on Maven Central

For Java 8, Include Conversant Disruptor from Maven Central:

```
<dependency>
  <groupId>com.conversantmedia</groupId>
  <artifactId>disruptor</artifactId>
  <version>1.2.12</version>
  <classifier>jdk8</classifier>
</dependency>
```
For Java 10:

```
<dependency>
  <groupId>com.conversantmedia</groupId>
  <artifactId>disruptor</artifactId>
  <version>1.2.12</version>
  <classifier>jdk10</classifier>
</dependency>
```

Or through the default Java 8 jar

```
<dependency>
  <groupId>com.conversantmedia</groupId>
  <artifactId>disruptor</artifactId>
  <version>1.2.12</version>
</dependency>
```

Java 7 is only supported in 1.2.10 and below.   

```
<dependency>
  <groupId>com.conversantmedia</groupId>
  <artifactId>disruptor</artifactId>
  <version>1.2.10</version>
  <classifier>jdk7</classifier>
</dependency>
```

Java 9 is no longer supported.

## Discussion Forum

Conversant Disruptor has a google group so you can follow releases and changes:   
https://groups.google.com/forum/#!forum/conversant-disruptor

