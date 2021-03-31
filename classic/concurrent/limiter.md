## 限流算法
高并发系统中为了保证服务稳定一般会采用限流算法防止因瞬时流量过大导致系统崩溃从而使服务不可用。限流算法通过将请求并发量在一定的时间窗口内限制在一定范围内来保护系统，一旦达到限制的范围则可以采取拒绝请求、排队等处理，从而保证系统的稳定性并尽可能的提升系统的吞吐量。

限流算法通常有三种实现：
- **计数器算法**：通过统计时间窗口内的请求数是否达到阈值，如果达到阈值则对后续请求做限流处理
- **漏桶算法**：将请求暂存在一个容器(桶)中，服务端按固定的速度处理请求，当桶满了时对后续请求做限流处理
- **令牌桶算法**：令牌以一定的速率放入容器(桶)中，请求需要先获取到令牌才能被服务端处理，获取令牌失败的请求做限流处理

### 计数器算法
计数器算法是一种最原始、朴素的算法，也是最容易实现的算法。计数器算法在每个时间窗口初始化的同时会初始化一个计数器，在该时间窗口内的所有请求都会被计数，如果在时间窗口内的请求数超过阈值，则对后续的请求做限流处理直到时间窗口到达，若时间窗口到达则会初始化另一个时间窗口并重置计数器。

计数器算法有两个比较严重的问题：
- 边界问题：请求在一个时间窗口的结束边界和下一个时间窗口的起始边界分别达到阈值不会触发限流处理，但是在瞬间的请求访问量却超过了阈值，可能会导致服务崩溃而不可用
- 突刺现象：请求量只在某个瞬间超过阈值而其他时刻是低于阈值的，会出现在超过阈值时对后续的请求做了限流处理而得不到服务端处理

计数器算法使用滑动窗口机制可以解决边界问题，滑动窗口将时间窗口划分为粒度更小的窗口，每次对一个小的窗口进行流量控制，每隔一个小的窗口时间则整个时间窗口会滑动一个小的时间窗口。每个小的时间窗口都有独自的计数器，把所有小窗口计数器的值加和即可得到整个时间窗口的计数值。
```shell

        +----------+----------+----------+----------+----------+----------+
        |          |          |          |          |          |          |
        |<- 200ms -><- 200ms -><- 200ms -><- 200ms -><- 200ms -><- 200ms ->
        |          |          |          |          |          |          |
        +----------+----------+----------+----------+----------+----------+
        |<------------------ 1s ------------------->|
                   |<-------------------- 1s ----------------->|
                              |<-------------------- 1s ----------------->|
```
如上，将 1s 的时间窗口划分为 5 个 200ms 的小窗口，每个小窗口计数阈值为：整个时间窗口阈值/小窗口数。每隔 200 ms 整个窗口向右滑动一个小窗口。
```go
```
### 漏桶(Leaky Bucket)算法
漏桶算法由流量容器、流量入口和出口组成，其中流量出口的流速即为期望的限速值。当突发流量到来时，流量出口的速率小于流量入口的速率，此时超出的流量会被容器缓存起来，如果存储的流量超过桶的容量则超出的流量将会限流处理。

漏桶具有流量整形的特点：流入桶的流量是非均匀的，而流出桶的流量是均匀的。
```shell

        ^                                                  ^
        |  +--+                     +--+                   |
        |  |--|                     |--|                   |
        |  |--|      +--+           |--|    leaky bucket   +--+  +--+  +--+  +--+
        |  |--|      |--|  +--+     |--|     ------->      |--|  |--|  |--|  |--|
        |  |--|      |--|  |--|     |--|     ------->      |--|  |--|  |--|  |--|
        |  |--|      |--|  |--|     |--|                   |--|  |--|  |--|  |--|
        +--+--+------+--+--+--+-----+--+--->  time         +--+--+--+--+--+--+--+---> time
```

```go
```
### 令牌桶(Token Bucket)算法
令牌桶设计用于支持突发流量，和漏桶不同的是令牌桶的桶中存放的是令牌，系统以一定的速率向桶中放入令牌，当桶满了之后多余的令牌会被丢弃，当请求到来时先到桶中获取令牌，如果桶中没有令牌则对该请求做限流处理。

```go
```
#### Warm Up
令牌桶算法是以一定的速率向桶中放入令牌，当系统长期处于低水位的情况下时，桶中就会堆积大量的令牌，当流量突然增加时会直接把系统拉升到高水位可能瞬间把系统压垮。通过 Warm Up（预热/冷启动）方式可以让流量缓慢增加到阈值上限，给冷系统一个预热的时间，避免冷系统被压垮。

```shell
   
             ^ throttling
             |
       cold  +                  /
    interval |                 /.
             |                / .
             |               /  .   ← "warmup period" is the area of the trapezoid between
             |              /   .     thresholdPermits and maxPermits
             |             /    .
             |            /     .
             |           /      .
      stable +----------/  WARM .
    interval |          .   UP  .
             |          . PERIOD.
             |          .       .
           0 +----------+-------+--------------→ storedPermits
             0 thresholdPermits maxPermits
```
在 Warm Up 模型中，横轴表示桶中的令牌数，竖轴表示获取令牌的时间间隔。当整个系统处于冷状态时，获取令牌的时间间隔为 ```coldInterval = coldFactor * stableInterval```，其中 coldFactor 固定为 3；当整个 Warm Up 阶段结束之后，系统进入稳定状态，此时获取令牌的时间间隔为 stableInterval。

假设有一条垂直的线表示限流器的状态(桶中令牌的数量)，当限流器未使用时则这条线会从 0 一直向右移动直到达到 maxPermits，整个移动的时间为 warmUpPeriod；当限流器使用时，则这条线从 X 移动到 X-K 的时间则为获取每个令牌所需的时间在 X ~ X-K 上的积分。

如果限流器令牌数从 maxPermits 到 thresholdPermits 的时间作为预热时间，则可以得到整个预热时间 warmUpPeriod 为梯形的面积 ```warmUpPeriod = 0.5 * (stableInterval + coldInterval) * (maxPermits - thresholdPermits)```
```go
```

#### Guava
Google 开源的 Guava 工具包中实现了流量控制算法，使用时需要在 POM 中引入依赖：
```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>28.1-jre</version>
</dependency>
```
Guava 中实现流量控制算法的核心类是 RateLimiter，RateLimiter 对外提供了一些流量控制相关的方法：
- ```setRate(double) void```：设置令牌桶每秒生成令牌的数量
- ```acquire(int) double```：获取指定数量的令牌，在拿到之前会一直阻塞，返回获取这些令牌所需的时长
- ```tryAcquire(Duration) boolean```：在指定超时时间内获取一个令牌，如果获取不到或者超时则返回 false

```java
public void testAquire(){
    RateLimiter limiter = RateLimiter.create(1);
    for(int i = 0; i < 10; i++){
        double waitTime = limiter.aquire(i)
        System.out.println("acq: " + i + "waitTime: " + waitTime);
    }
}
```
SmoothRateLimiter 是 RateLimiter 的实际实现，其包含两种限流模式：
- ```SmoothBursty```：突发模式，令牌以恒定的速度生成，允许一定的突发流量
- ```SmoothWarmingUp```：预热模式，获取令牌的时间间隔随着系统的预热而减少直到达到一个稳定值

SmoothRateLimiter 定义了几个重要的属性：
```java
/** The currently stored permits. */
double storedPermits;

/** The maximum number of stored permits. */
double maxPermits;

/**
* The interval between two unit requests, at our stable rate. E.g., a stable rate of 5 permits
* per second has a stable interval of 200ms.
*/
double stableIntervalMicros;

/**
* The time when the next request (no matter its size) will be granted. After granting a request,
* this is pushed further in the future. Large requests push this further than small requests.
*/
private long nextFreeTicketMicros = 0L; // could be either in the past or future
```
RateLimiter 在初始化时会调用 setRate 方法设置令牌桶每秒生成的令牌数量，其内部是通过调用 SmoothRateLimiter 的 doSetRate 来实现：
```java
@Override
final void doSetRate(double permitsPerSecond, long nowMicros) {
  resync(nowMicros);
  double stableIntervalMicros = SECONDS.toMicros(1L) / permitsPerSecond;
  this.stableIntervalMicros = stableIntervalMicros;
  doSetRate(permitsPerSecond, stableIntervalMicros);
}
```
先通过调用 resync 方法生成令牌以及更新下一期令牌生成时间，然后更新 stableIntervalMicros，最后调用重写的方法 doSetRate 来设置 storedPermits 和 maxPermits。

在突发模式中，桶中的初始令牌数 storedPermits 为 0，桶中最大的令牌数 maxPermits 为 ```maxBurstSeconds * permitsPerSecond```(最大持续时间 * 每秒放入桶中的令牌)，而在桶中的令牌被使用时能够通过 permitsPerSecond 动态的调整 storedPermits：
```java
void doSetRate(double permitsPerSecond, double stableIntervalMicros) {
    double oldMaxPermits = this.maxPermits;
    maxPermits = maxBurstSeconds * permitsPerSecond;
    if (oldMaxPermits == Double.POSITIVE_INFINITY) {
        // if we don't special-case this, we would get storedPermits == NaN, below
        storedPermits = maxPermits;
    } else {
        storedPermits = 
            (oldMaxPermits == 0.0)
                ? 0.0 // initial state
                : storedPermits * maxPermits / oldMaxPermits;
    }
}
```
在预热模式中，桶在初始时处于冷状态，桶中的令牌数 storedPermits 为 ```thresholdPermits + 2.0 * warmupPeriodMicros / (stableIntervalMicros + coldIntervalMicros)```
```java
void doSetRate(double permitsPerSecond, double stableIntervalMicros) {
    double oldMaxPermits = maxPermits;
    double coldIntervalMicros = stableIntervalMicros * coldFactor;
    thresholdPermits = 0.5 * warmupPeriodMicros / stableIntervalMicros;
    maxPermits =
        thresholdPermits + 2.0 * warmupPeriodMicros / (stableIntervalMicros + coldIntervalMicros);
    slope = (coldIntervalMicros - stableIntervalMicros) / (maxPermits - thresholdPermits);
    if (oldMaxPermits == Double.POSITIVE_INFINITY) {
        // if we don't special-case this, we would get storedPermits == NaN, below
        storedPermits = 0.0;
    } else {
    storedPermits =
        (oldMaxPermits == 0.0)
            ? maxPermits // initial state is cold
            : storedPermits * maxPermits / oldMaxPermits;
    }
}
```
构造方法中调用 setRate 初始化了 storedPermits 和 maxPermits；当请求到来时需要调用 acquire 方法获取令牌：
```java
```
### 分布式限流算法
### Ref
- http://xiaobaoqiu.github.io/blog/2015/07/02/ratelimiter/
- https://www.cnblogs.com/forezp/p/11407113.html
- https://github.com/google/guava/blob/master/guava/src/com/google/common/util/concurrent/RateLimiter.java
- https://github.com/alibaba/Sentinel/wiki
- https://github.com/Netflix/Hystrix/wiki