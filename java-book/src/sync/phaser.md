## Phaser

Phaser又称“阶段器”，用来解决控制多个线程分阶段共同完成任务的情景问题。它与 CountDownLatch 和 CyclicBarrier 类似，都是等待一组线程完成工作后再执行下一步工作。

Phaser 可以动态的调整需要同步的线程数，相较 CountDownLatch 和 CyclicBarrier 更加灵活。Phaser 提供了增加或者减少同步线程数的方法：
```java
// 注册 1 个
register()
// 注册多个
bulkRegister(n)
// 解注册
arriveAndDeRegister()
```
Phaser 提供了常用的方法，这些方法可以很好的替代 CountDownLatch 和 CyclicBarrier 的功能：
- `arrive()`
- `awaitAdvance(phase)`
- `arriveAndAwaitAdvance()`