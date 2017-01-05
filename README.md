<img src="https://github.com/conversant/disruptor/blob/master/src/main/resources/ConversantDisruptorLogo.png?raw=true">

# Conversant ConcurrentQueue and Disruptor BlockingQueue

Disruptor is the highest performing intra-thread transfer mechanism available in Java.  Conversant Disruptor is the highest performing implementation of this type of ring buffer queue because it has almost no overhead and it exploits a particularly simple design. 

# Getting Started

Simply run the maven build to build and use the package.

```$ mvn -U clean package```

# Conversant Disruptor is on Maven Central

Maven Java 8 users can incorporate Conversant Disruptor the usual way:

```
<dependency>
  <groupId>com.conversantmedia</groupId>
  <artifactId>disruptor</artifactId>
  <version>1.2.10</version>
  <classifier>jdk8</classifier>
</dependency>
```
OR

```
<dependency>
  <groupId>com.conversantmedia</groupId>
  <artifactId>disruptor</artifactId>
  <version>1.2.10</version>
</dependency>
```

Java 7 is also supported

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

