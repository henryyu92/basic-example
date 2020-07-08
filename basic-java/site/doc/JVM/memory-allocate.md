## 内存分配

对象的内存分配主要是在堆上的分配，在分代模型中新生对象通常会分配在新生代中，当对象大小超过阈值时会直接分配到老年代上。对象的分配规则并不是固定的，而是取决于虚拟机使用的垃圾收集器以及垃圾收集器中与内存相关的参数设定。

### 对象优先在 Eden 分配

大部分情况下，对象在新生代 Eden 区中分配，当 Eden 区中没有足够空间进行分配时，虚拟机将发起一次 Minor GC。HotSpot 虚拟机提供了参数 ```-XX:+PrintGCDetails``` 用于打印发生垃圾收集器时的日志。

### 大对象直接进入老年代

所谓的大对象是指需要大量连续内存空间的 Java 对象，最典型的大对象就是那种很长的字符串以及数组；经常出现大对象容易导致内存还有不少空间时就提前触发垃圾收集以获得足够的连续空间来“安置”它们。

虚拟机提供```-XX:PretenureSizeThreshold```参数，大于这个设置值得对象直接在老年代分配，这样就可以避免在 Eden 区以及两个 Survivor 区之间发生大量的内存复制；该参数只对 Serial 和 ParNew 收集器有效，Parallel Scavenge 收集器不支持这个参数。

```java
// -verbose:gc -Xms20m -Xmx20m -Xmn10m 
// -XX:+PrintGCDetails -XX:SurvivorRatio=8 
// -XX:PretenureSizeThreshold=3145728
public static void testPretenureSizeThreshold(){
    byte[] allocation;
    allocation = new byte[4 * 1024 * 1024];
}
```

#### 长期存活的对象将进入老年代

虚拟机给每个对象定义了一个对象年龄计数器存储在对象头中，如果对象在 eden 区出生并经过第一次 Minor GC 后仍然存活并且能被 Survivor 容纳的话，将被移动到 Survivor 空间中并且对象年龄设为 1；对象在 Survivor 区中每“熬过”一次 Minor GC 年龄就增加 1 岁，当它的年龄增加到一定程度（默认是 15 岁）就将会被晋升到老年代中；对象晋升到老年代的年龄阈值，可以通过参数```-XX:MaxTenuringThreshold=15```设置

```java
// -verbose:gc -Xms20m -Xmx20m
// -Xmn10m -XX:+PrintGCDetails -XX:SurvivorRatio=8
// -XX:MaxTenuringThreshlod=1 -XX:+PrintTenuringDistribution
public static void testTenuringThreshold(){
    byte[] allocation1 = new byte[4 * 1024 * 1024];
    byte[] allocation2 = new byte[4 * 1024 * 1024];
    byte[] allocation3 = new byte[4 * 1024 * 1024];

    allocation3 = null;

    byte[] allocation4 = new byte[4 * 1024 * 1024];

}
```

#### 动态年龄判定

为了更好地适应不同程序的内存状况，虚拟机并不是永远地要求对象的年龄必须达到了 MaxTenuringThreshold 才能晋升老年代，如果在 Survivor 空间中相同年龄所有对象大小的总和大于 Survivor 空间的一半，年龄大于或等于该年龄的对象就可以直接进入老年代，无需等到 MaxTenuringThreshold 中要求的年龄。

#### 空间担保

在发生 Minor GC 之前，虚拟机会先检查老年代最大可用的连续空间是否大于新生代所有对象总空间；如果这个条件成立，那么 Minor GC 可以确保是安全的；如果不成立则虚拟机会查看 HandlePromotionFailure 设置值是否允许担保失败；如果允许，那么会继续检查老年代最大可用的连续空间是否大于历次晋升到老年代对象的平均大小；如果大于，将尝试着进行一次 Minor GC，尽管这次 Minor GC 是有风险的；如果小于，或者 HandlePromotionFailure 设置不允许冒险，那这时也要改为进行一次 Full GC。
		
新生代使用复制收集算法，但为了内存利用率，只使用其中一个 Survivor 空间来作为轮换备份，因此当出现大量对象在 Minor GC 后仍然存活的情况（最极端的情况就是内存回收后新生代中所有对象都存活），就需要老年代进行分配担保，把 Survivor 无法容纳的对象直接进入老年代。新生代Minor GC 之后还要多少对象存活在内存回收之前是无法知道的，所以只好取之前每一次回收晋升到老年代对象容量的平均大小值作为经验值，与老年代剩余空间进行比较，决定是否进行 Full GC 来让老年代腾出更多空间。

取平均值比较其实仍然是一种动态概率的手段，如果某次 Minor GC 存活后的对象突增，远远高于平均值的话，依然会导致担保失败(Handle Promotion Failure)。如果出现了担保失败，那就只好在失败之后重新发起一次 Full GC。

建议将 HandlePromotionFailure 开关打开，避免 Full GC 过于频繁；jdk 1.6 update 24 之后 HandlePromotionFailure 参数不会再影响到虚拟机的空间分配策略，该策略变更为：**只要老年代的连续空间大于新生代对象总大小或者历次晋升的平均大小就会进行 Minor GC，否则将将进行 Full GC**。


**[Back](../../)**