## 网络 I/O 模型

Linux 的内核将所有的外部设备都看做文件，对文件的读写操作会调用内核提供的系统命令，返回一个文件描述符 (file descriptor, fd)，文件描述符是一个地址，指向内核中的一个结构体，该结构体存储了文件路径、数据区等一些属性。

Linux 网络编程支持 5 中 I/O 模型：阻塞 I/O 模型、非阻塞 I/O 模型、I/O 复用模型、信号驱动 I/O 模型、异步 I/O 模型。

操作系统的核心是内核，独立与普通的应用程序，可以访问受保护的内存空间，也有访问底层硬件设备的所有权限。现代操作系统都是采用虚拟存储器，为了保证用户进程不能直接操作内核，操作系统将虚拟空间划分为内核空间和用户空间。

### 阻塞 I/O 模型

应用进程调用内核命令 ```recvfrom```，其系统调用直到数据包到达且被复制到应用进程的缓冲区中或者发生错误才返回，在此期间应用进程都是被阻塞的，因此称为阻塞 I/O 模型
```



```

### 非阻塞 I/O 模型

recvfrom 从应用层到内核的时候，如果缓冲区没有数据就直接返回 EWOULDBLOCK 错误，一般都对非阻塞 I/O 模型进行轮询检查这个状态，看内核是不是有数据到来，这个过程中应用进程并没有一直阻塞等待数据到来，因此称为非阻塞 I/O 模型 
```java






```

### I/O 复用模型

Linux 提供 select/poll，进程通过将一个或多个 fd 传递给 select 或 poll 系统调用，这样 select/poll 就可以侦测多路 fd 是否处于就绪状态，而进程只阻塞在 select 操作上。

select/poll 是顺序扫描 fd 是否就绪，而且支持的 fd 数量有限，当少量 fd 活跃时效率较低；Linux 提供了 epoll 系统调用，epoll 使用信号驱动方式代替顺序扫描，当 fd 就绪时立即调用回调函数 rollback，因此性能会更高。

```java
```

### 信号驱动 I/O 模型

首先开启套接口信号驱动 I/O 功能，并通过系统调用 sigaction 执行一个信号处理函数(此系统调用立即返回，进程继续工作，它是非阻塞的)，当数据准备就绪时，就为该进程生成一个 SIGIO 信号，通过信号回调通知应用程序调用 recvfrom 来读取数据。

```
```

### 异步 I/O 模型


```
```


## Java 网络编程

Java 的网络模型支持三种 I/O 模型：
- BIO：同步阻塞模型，服务器为客户端的每个连接启动一个线程处理，这种方式在高并发时受限于系统的线程数，对于短连接来说会频繁的创建和销毁线程，消耗大量系统资源
- NIO：同步非阻塞模型，客户端发起的请求会注册到多路复用器上，服务器轮询多路复用器，将轮询到的请求在一个线程中处理
- AIO：异步非阻塞模型，采用 Proactor 模式，由操作系统完成之后通知服务端程序处理请求，一般适用于连接数较多且连接时间较长的应用


BIO 以流的方式处理数据，而 NIO 以块的方式处理数据，效率会有很大提升；BIO 是阻塞的，而 NIO 是非阻塞的；NIO 基于 Channel 和 Buffer 进行操作，数据总是从 Channel 读取到 Buffer 中或者从 Buffer 写入到 Channel 中，使用 Selector 监听注册的多个通道的事件使得可以使用一个线程就可以监听多个客户端

```
                                                   +------------+
                                                   |  Selector  |
                                                   +------------+
                                                         ^
                                                         | Register
                                                +-------------------+
  +---------+ read/write +--------+  read/write |   +-----------+   | read/write +--------+ read/write +---------+
  | Handler |<---------->| Buffer |<----------->|   |  Channel  |   |<---------->| Buffer |<---------->| Handler |
  +---------+            +--------+             |   +-----------+   |            +--------+            +---------+
                                                |                   |
  +---------+ read/write +--------+  read/write |   +-----------+   | read/write +--------+ read/write +---------+
  | Handler |<---------->| Buffer |<----------->|   |  Channel  |   |<---------->| Buffer |<---------->| Handler |
  +---------+            +--------+             |   +-----------+   |            +--------+            +---------+
                                                |                   |
  +---------+ read/write +--------+  read/write |   +-----------+   | read/write +--------+ read/write +---------+
  | Handler |<---------->| Buffer |<----------->|   |  Channel  |   |<---------->| Buffer |<---------->| Handler |
  +---------+            +--------+             |   +-----------+   |            +--------+            +---------+
                                                +-------------------+


```
每个 Channel 都对应一个 Buffer；Selector 对应一个线程，一个线程对应多个 channel；Selector 根据 channel 的不同事件在各个 Channel 上切换；

### Buffer

Buffer 是一个缓冲区的概念，包含写入和需要读出的数据。NIO 中所有的数据都是用 Buffer 处理的，读数据时直接从 Buffer 中读，写数据时直接写到 Buffer 中。

Buffer 本质上是一个可以读写数据的内存块，Channel 的数据读写必须通过 Buffer 才能实现。Buffer 内部定义了 3 个维护数据读写位置的变量：
- ```positoin```：Buffer 中下一个读或者写的元素的索引
- ```limit```：Buffer 的当前终点，不能对超过终点位置进行读写
- ```capacity```：Buffer 可以容纳的最大元素个数，在 Buffer 创建的时候指定

除了 Java 基本类型对应的 Buffer 外，NIO 提供了 ```MappedByteBuffer``` 让文件直接映射在堆外内存。
```java
```

对文件的修改同步由  NIO 完成。

NIO 还支持通过多个 Buffer 完成读写操作
```java
```

### Channel

和 Java 传统 I/O Stream 不同，Channel 是双工的，可以同时进行读写。因为 Channel 是全双工的，所以能更好的映射底层操作系统的 API。

Channel 只能从 Buffer 中读取或者写入数据。

Channel 提供了 FileChannle、DatagramChannel、ServerSocketChannel、SocketChannel 等实现类，其中 FileChannel 用于文件的数据读写，DatagramChannel 用于 UDP 的数据读写，ServerSocketChannle 和 SocketChannel 用于 TCP 的数据读写。


### Selector

Selector 是一个多路复用器，Channel 将事件注册到 Selector 上后会被监听，当注册的事件发生时 Selector 就能感知到，然后将事件发生的 Channel 返回到后续操作。Java 使用 epoll 使得只需要一个线程负责 Selector 的监听，就可以实现上千万的客户端接入。

Selector 上可以注册多个 Channel 的多个事件，一个 Channel 也可以注册到多个 Selector 上。Channel 注册到 Selector 上会对应一个 SelectionKey，当 Channle 上有事件发生时可以通过 ```selectedKeys``` 获取到对应的 ```SelectionKey```，然后通过 ```SelectionKey``` 得到对应的 Channel，之后就可以通过 Channel 获取到数据：
```java
```

SelectionKey 表示 Selector 和 Channel 的注册关系，共有 4 中：
- OP_ACCEPT：有新的网络连接，16
- OP_CONNECT：连接已建立，8
- OP_READ：读操作，1
- OP_WRITE：写操作，4

Java 中常用的零拷贝有 mmap（内存映射）和 sendFile。 mmap 通过内存映射，将文件映射到内核缓冲区，用户空间和内核空间可以共享内核空间的数据，减少用户空间到内核空间的数据拷贝(4 次减少到 3 次)。sendFile 是数据不经过用户态，直接从内核缓冲区进入到 SocketBuffer，在减少数据拷贝的同时也减少了状态上下文切换。

mmap 适合小数据量读写，sendFile 适合大文件传输；mmap 需要 4 次上下文切换，3 次数据拷贝，sendFile 需要 3 次上下文切换，最少 2 次数据拷贝；sendFile 可以利用 DMA 方式，减少 CPU 拷贝， mmap 则不能，必须从内核拷贝到 socket 缓冲区

















http://www.52im.net/thread-306-1-1.html