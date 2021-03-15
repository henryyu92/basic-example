## 内存模型

Java 内存模型（Java Memory Model, JMM）的定义了程序中各个变量的访问规则，即虚拟机中将变量存储到内存和从内存中取出变量这样的底层细节。

JMM 规定了所有的变量都存储在主内存(Main Memory)，每个线程有自己的工作内存(Worker Memory)，线程的工作内存中保存了该线程使用到的变量的主内存副本，线程对变量的所有操作(读取、赋值等)都必须在工作内存中进行而不能直接读写主内存中的变量，不同线程之间也无法直接访问对方工作内存中的变量，线程间变量值的传递需要通过主内存来完成。

- **原子性**：原子性指内存的操作在一个动作内完成，没有中间过程，JMM 保证对变量的每次操作都是原子的
- **可见性**：可见性指的是线程对内存的修改能立即被其他线程感知
- **有序性**：有序性指的是单个线程的操作是有序的，而多线程之间是顺序一致的

现代操作系统为了提升效率，在运行指令时会进行指令重排。

#### 指令重排

### as-if-serial 语义
为了保证单个线程内的操作有序，as-if-serial 语义规定了重排序后的指令执行结果不能改变运行结果，这使得存在数据依赖的操作不会被重排序。

### 先行发生原则

先行发生(hapends-before)是 Java 内存模型中定义的两项操作之间的偏序关系，**如果说操作 A 先行发生于操作 B，其实就是说在发生操作 B 之前，操作 A 产生的影响能被操作 B 观察到**。

Java 内存模型中存在“天然的”先行发生关系：
- 在一个线程内，按照代码控制流顺序前面的操作先行发生于后面的操作
- 对一个锁的 unlock 操作先行发生于后面对同一个锁的 lock 操作
- 对一个 volatile 变量的写操作先行发生于后面对这个变量的读操作
- Thread 对象的 start() 方法先行发生于此线程的每一个动作
- 线程的所有操作都先行发生于对此线程的终止检测
- 对线程的 interrupt() 方法的调用先行发生于被中断线程的代码检测到中断事件的发生
- 一个对象的初始化完成先行发生于它的 finalize() 方法
- 如果 A 操作先行发生于 B 操作，B 操作先行发生于 C 操作，则 A 操作先行发生于 C 操作

每个先行发生规则对应一个或多个编译器和处理器的重排序规则，封装了 JMM 提供的内存可见性规则和实现，从而可以在一些情况下可以不用考虑多线程情况下的可见性问题。

### volatile

volatile 关键字是 Java 虚拟机提供的最轻量级的同步机制，Java 内存模型对 volatile 专门定义了一些特殊的访问规则使其具备两种特性：
- **可见性**：一个线程修改了 valatile 修饰的变量的值对其他线程来说是可见的，volatile 的可见性保证变量修改后立即同步到主内存而每次读取变量需要立即从主内存读取
- **禁止指令重排序优化**：volatile 关键字修饰的变量在写操作时会在本地代码中插入内存屏障指令来保证处理器不会发生重排序

### final
对于 final 关键字，编译器和处理器要遵守两个重排序规则：
- 在构造函数内对一个 final 变量的写入与随后把这个被构造的对象的引用赋值给一个变量这两个操作不能重排序
- 初次读一个包含 final 变量的对象的引用与随后初次读 final 变量这两个操作不能重排序


### 双重检查锁定
双重检查锁定是一种延迟实例化的技术，通过双重检查锁定来降低同步的开销，但是双重检查锁定很容易会错误使用。

双重检查锁定的错误用法：
```java
public class DoubleCheckLocking{

    private static Instance instance;
    public static Instance getInstance(){
        // 不为 null 时 instance 可能并没有完成初始化
        if(instance == null){
            synchronized(DoubleCheckLocking.class){
                // 加锁之前其他线程有可能创建了 instance 实例
                if(instance == null){
                    instance = new Instance();
                }
            }
        }
        return instance;
    }
}
```
首先检查 instance 实例是否为 null，如果是则加锁再次判断创建实例，如果不是则直接返回；双重加锁降低了锁的粒度而降低 synchronized 的性能开销，但是存在一个严重的问题：**判断对象不为 null 时对象可能还未完全初始化！**

Java 中创建对象实例可以理解为 3 步：分配内存空间、初始化对象、将变量引用指向对象内存地址。由于重排序这 3 步的执行顺序可能发生乱序，因此会导致变量引用指向了对象的内存地址但是该内存还没有初始化，此时访问该内存不会返回 null。

为了避免由于指令重排导致双重检查锁定延迟初始化对象发生错误，有两种方案：volatile 关键字 和 静态内部类。

volatile 关键字可以禁止指令重排，因此可以避免发生错误
```java
public class DoubleCheckLocking{

  private static volatile Instance instance;
  public static Instance getInstance(){
    if(instance == null){
        synchronized(DoubleCheckLocking.class){
            if(instance == null){
                instance = new Instance();
            }
        }
    }
    return instance;
  }
}
```

静态类在加载的时候已经完成初始化并且 JVM 保证多线程初始化时只会有一个线程能完成初始化
```java
public class InstanceFactory{
  // 类在加载时会初始化静态变量
  private static class InstanceHolder{
    public static Instance instance = new Instance();
  }

  public static Instance getInstance(){
	return InstanceHolder.instance;
  }
}
```

**[Back](../)**