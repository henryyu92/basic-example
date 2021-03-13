### GC 日志
```shell
[GC (Allocation Failure) [PSYoungGen: 6248K->792K(9216K)] 6248K->4896K(19456K), 0.0014864 secs] 
[Times: user=0.00 sys=0.00, real=0.00 secs]
```
- ```[GC (Allocation Failure)``` 表示发生了 GC，原因是对象在 Eden 区分配失败
- ```[PSYoungGen``` 表示采用 Parallel Scavenge 收集器的新生代发生了 GC
  - ```[DefNew```：表示使用 Serial 收集器的新生代(Default New Generation)发生了 GC
  - ```[ParNew```：表示使用 ParNew 收集器的新生代(Parallel New Generation) 发生了 GC	
  - ```[Tenured```：表示老年代发生了 GC
  - ```[Perm```：表示永久区发生了 GC
- ```6248K->792K(9216K)```：表示 GC 前该内存区域已使用容量->GC后该内存区域已使用容量(该内存区域总量，不含其中一个 survivor)
- ```6248K->4896K(19456K)```：表示 GC 前 Java 堆已使用容量->GC 后 Java 堆已使用容量(Java 堆总容量，不含其中一个 survivor)
- ```0.0014864 secs```：表示该区域 GC 所用时间
- ```[Times: user=0.00 sys=0.00, real=0.00 secs]```：表示用户态消耗的 GPU 时间、内核消耗 CPU 时间(不包含 I/O)和总时间(包括I/O)
### GC 信息

#### GC 跟踪
- ```-XX:+PrintGC```：打印 GC 信息
- ```-XX:+PrintGCDetails```：打印 GC 详细信息
- ```-XX:+PrintGCTimeStamps```：打印 GC 停顿耗时
- ```-XX:+PrintTenuringDistribution```：打印 GC 后新生代各个年龄对象的大小
- ```-XX:+PrintHeapAtGC```：GC 之后打印堆信息
- ```-Xloggc:log/gc.log```：指定 GC log 的位置，以文件输出
- ```-XX:+UseGCLogFileRotation```：gc 日志滚动
- ```-XX:NumberOfGCLogFiles=10```：gc 日志保留数量
- ```-XX:GCLogFileSize=100M```：gc 日志文件大小
- ```-XX:+HeapDumpOnOutOfMemoryError```：OOM 时导出堆到文件(默认导出到项目根目录下)
- ```-XX:+HeapDumpPath=/dump/path```：OOM 时导出文件的路径
- ```-XX:OnOutOfMemoryError=/path/of/shell```：OOM 时执行的脚本
- ```-XX:+TraceClassLoading```：打印类加载信息
- ```-XX:+TraceClassUnloading```：打印类卸载信息

```shell
-XX:+PrintGCDetails \
-XX:+PrintHeapAtGC \
-XX:+PrintGCTimeStamps \
-XX:+PrintTenuringDistribution \
-Xloggc:log/gc.log \
-XX:+HeapDumpOnOutOfMemoryError \
-XX:+HeapDumpPath=d:/a.dump \
-XX:OnOutOfMemoryError=D:/tools/jdk1.7_40/bin/printstack.bat %p
```

### 内存设置参数
- ```-Xmx2g```：设置堆的最大值，如 -Xmx2g
- ```-Xms2g```：设置堆的最小值，推荐堆最小值和最大值一致，避免堆的收缩
- ```-Xmn256m```：设置新生代大小
- ```-XX:NewRatio=3/8```：新生代(eden+2*s)和老年代的比值，官方推荐新生代站总推内存的 3/8
- ```-XX:SurvivorRatio=8```：表示 eden 占新生代的 80%
- ```-Xss128```：设置Java栈的最大值，如 -Xss128k
- ```-XX:MaxDirectMemorySize=n```：设置直接内存大小，默认和堆大小一致

```shell
-Xmx2g -Xms2g -Xss128k -XX:NewRatio=1/2 -XX:SurvivorRatio=9
```

#### 垃圾收集器常用参数
- ```-XX:+UseSerialGC``` - 虚拟机运行在 Client 模式下的默认值，打开此开关后，使用 Serial + Serial Old 的收集器组合进行垃圾回收
- ```-XX:+UseParNewGC``` - 打开此开关后，使用 ParNew + Serial Old 的收集器组合进行垃圾回收
- ```-XX:+UseConcMarkSweepGC``` - 打开此开关后，使用 ParNew + CMS + Serial Old 的收集器组合进行内存回收。Serial Old 收集器作为 CMS 收集器出现 Concurrent Mode Failure 时的后备收集器
- ```-XX:+UseParallelGC``` - 虚拟机在 Server 模式下的默认值，打开此开关后，使用 Parallel Scavenge + Serial Old 的收集器组合进行内存回收
- ```-XX:+UseParallelOldGC``` - 打开此开关后，使用 Parallel Scavenge + Parallel Old 的收集器组合进行内存回收
- ```-XX:PretenureSizeThreshold=m``` - 直接晋升到老年代的对象大小，设置这个参数后，大于这个参数的对象直接在老年代分配，如 -XX:PretenureSizeThreshold=3145728(而不是 3m)
- ```-XX:MaxTenuringThreshold=15``` - 晋升到老年代的对象年龄，每个对象在坚持过一次 Minor GC 之后，年龄就会增加 1，当超过这个参数值时就进入老年代，默认 15
- ```-XX:+UseAdaptiveSizePolicy``` - 动态调节 Java 堆中各个区域的大小以及进入老年代的年龄
- ```-XX:+HandlePromotionFailure``` - 是否允许分配担保失败，即老年代的剩余空间不足以应付新生代的整个 Eden 和 Survivor 区的所有对象都存活的极端情况
- ```-XX:ParallelGCThreads=n``` - 设置并行 GC 时进行内存回收的线程数

### JVM 监控
#### jps
jps 列出正在运行的虚拟机进程并显示进程的主类名称和进程的 ID，进程 ID 用于后续对 JVM 的分析
```shell
jps | grep TestMain
```
#### jstat
用于监视虚拟机各种运行状态信息，可以显示本地或远程虚拟机进程中的类加载、内存、垃圾收集等运行数据
```shell
jstat [option] vmid [interval][s|ms] [count]]
  - option		表示查询的虚拟机信息
    -calss		监视类装载、卸载数量、总空间以及类装载所耗费的时间
    -gc			监视 Java 堆状况，包括 Eden区、两个 Survivor 区、老年代、永久代等的容量，已使用空间、GC 时间合计等信息
    -gccapacity		监视内容与 -gc 基本相同，但输出主要关注 Java 堆各个区域使用到的最大、最小空间
    -gcutil		监视内容与 -gc 基本相同，但输出主要关注已使用空间占总空间的百分比
    -gcnew		监视新生代 GC 状况
    -gcnewcapacity	监视内容与 -gcnew 基本相同，但输出主要关注使用到的最大、最小空间
    -gcold		监视老年代 GC 状况
    -gcoldcapacity	监视内容与 -gcold 基本相同，但输出主要关注使用到的最大、最小空间
    -compiler		输出 JIT 编译器编译过的方法、耗时等信息
    -printcompilation	输出已经被 JIT 编译的方法
  - vmid		表示进程 ID
  - interval		表示查询间隔，默认单位是 ms
  - count		表示查询次数
```
使用 jstat -gc 查看虚拟机的堆信息：
```shell
jstat -gc 2764 250 20

S0C    S1C    S0U    S1U      EC       EU        OC         OU       MC     MU    CCSC   CCSU   YGC     YGCT    FGC    FGCT     GCT   
1024.0 3072.0 732.9   0.0   91136.0  31864.5   157184.0   57079.0   20608.0 20035.9 2176.0 2065.7     52    0.135   0      0.000    0.135
1024.0 3072.0 732.9   0.0   91136.0  31864.5   157184.0   57079.0   20608.0 20035.9 2176.0 2065.7     52    0.135   0      0.000    0.135
1024.0 3072.0 732.9   0.0   91136.0  31864.5   157184.0   57079.0   20608.0 20035.9 2176.0 2065.7     52    0.135   0      0.000    0.135
1024.0 3072.0 732.9   0.0   91136.0  31864.5   157184.0   57079.0   20608.0 20035.9 2176.0 2065.7     52    0.135   0      0.000    0.135
1024.0 3072.0 732.9   0.0   91136.0  31864.5   157184.0   57079.0   20608.0 20035.9 2176.0 2065.7     52    0.135   0      0.000    0.135
1024.0 3072.0 732.9   0.0   91136.0  31864.5   157184.0   57079.0   20608.0 20035.9 2176.0 2065.7     52    0.135   0      0.000    0.135
```
- ```S0C``` 表示 S0 内存大小 
- ```S0U``` 表示 S0 内存使用大小
- ```S1C``` 表示 S1 内存大小
- ```S1U``` 表示 S1 内存使用大小
- ```EC``` 表示 Eden 区内存大小
EU
OC
OU
MC
MU
CCSC
CCSU
YGC
YGCT
FGC
FGCT
GCT
#### jinfo
实时查看和调整虚拟机各项参数，使用 jinfo -flag [+|-]name 或者 jinfo -flag name=value 可以修改一些运行期可写的虚拟机参数
```shell
```
#### jmap
jmp 命令用于生成堆转储快照
```shell
jmap -dump <vmid>
```
#### jstack
用于生成虚拟机当前时刻的线程快照(当前虚拟机内每一条线程正在执行的方法堆栈的集合)，生成线程快照的主要目的是定位线程出现长时间停顿的原因，如线程间死锁、死循环、请求外部资源导致的长时间等待等都是导致线程长时间停顿的常见原因。
```shell
jstack [option] vmid
  - option
      -l 表示除堆栈外，还显示关于锁的附加信息

jps | grep TestMain | 
```
#### top
#### netstat
#### iostat

### CPU 负载过高异常排查
- 定位高负载进程：使用 top 命令查看 ```load average``` 确认服务器负载较重并找到占用 CPU 较高的 pid
- 查看进程下对应的线程使用 CPU 情况，使用 ```top -Hp <pid>``` 可以查看 pid 进程下所有线程的 CPU 使用情况，确定导致 CPU 负载过高的线程
- 将线程 id 转换为十六进制 ```printf '%x\n' 线程id```
- 将堆栈信息 dump 下来并查看对应线程的信息，使用 ```jstack <pid> | grep 线程 id``` 查看堆栈中线程id(jstack 中的 nid)对应的线程状态

原因分析：
- **内存消耗过大导致 Full GC 次数较多**：jstack 命令查找到的线程名为 "VM Thread" 表示为 GC 回收线程，通过 ```jstat -gcutil <pid> <interval> <count>``` 查看进程 GC 变化，如果发现 Full GC 时间较长且频繁则将其内存快照 dump 出来分析，使用 ```jmap -dump:live,format=b,file=dimp.hprof pid``` 之后对 dump 出来的内存快照进行分析
- **程序中有消耗 CPU 操作**：通过 jstack 命令可以定位到代码，此时可以查看代码确定是否是需要消耗大量 CPU 的复杂算法还是由于代码问题导致死循环
- **激烈的锁竞争**：多个线程在不断的尝试获取锁


**[Back](../)**