## 网络编程

Java 网络编程支持三种 I/O 模型：
- BIO：同步阻塞模型，服务器为客户端的每个连接启动一个线程处理，这种方式在高并发时受限于系统的线程数，对于短连接来说会频繁的创建和销毁线程，消耗大量系统资源
- NIO：同步非阻塞模型，客户端发起的请求会注册到多路复用器上，服务器轮询多路复用器，将轮询到的请求在一个线程中处理
- AIO：异步非阻塞模型，采用 Proactor 模式，由操作系统完成之后通知服务端程序处理请求，一般适用于连接数较多且连接时间较长的应用


### BIO

BIO 使用独立的 Acceptor 线程负责监听客户端的连接，客户端请求时为每个客户端创建连接并创建新的线程处理链路，处理完成之后通过字节流返回给客户端，然后断开连接销毁线程。

```
                +--------------+
 +--------+     |              |<---> Thread
 | Client |---->|              |
 +--------+     |              |<---> Thread
                |   Acceptor   |
 +--------+     |              |<---> Thread
 | Client |---->|              |
 +--------+     |              |<---> Thread
                +--------------+
```
BIO 的问题在与对于每个连接都需要创建新的线程来处理链路，当客户端较多且为长连接时，线程数数会急剧膨胀，造成系统性能急剧下降。

Socket 是阻塞的，且是以流的形式读写的，当一个连接由于网络问题数据传输比较慢，则在读取数据期间线程一直处于空闲。

### NIO

NIO 是基于 Selector、Channel 和 Buffer 处理网络连接和读写。其中 Selector 是多路复用的 Acceptor，所有 Channel 都会向 Selector 注册并被监听，当有连接或者读写事件发生时才会处理；Channel 是一个双向数据管道，Socket 中的数据通过 Channel 进行读写；Buffer 是一个缓冲，数据都是通过 Buffer 才能从 Channel 中读写。

```
           r/w           r/w             register +----------+
  Handler <-----> Buffer <-----> Channle -------->|          |
                                                  |          |
           r/w           r/w             register |          |
  Handler <-----> Buffer <-----> Channle -------->| Selector |
                                                  |          |
           r/w           r/w             register |          |
  Handler <-----> Buffer <-----> Channle -------->|          |
                                                  +----------+
```
Selector 作为 Acceptor 线程监听注册的 Channel 的连接以及读写事件，当发生事件时才需要进行创建连接、读取数据等操作；Selector 是基于 epoll 实现，当对应的 Channel 上有事件发生才会触发。

每个 Channel 都有一个对应的 Buffer 与之交换数据，从 Channel 中读取数据使用 read 方法，往 Channel 中写数据使用 write 方法。 

#### Selector

Selector 是一个多路复用器，Channel 将事件注册到 Selector 上后会被监听，当注册的事件发生时 Selector 就能感知到，然后将事件发生的 Channel 返回到后续操作。Java 使用 epoll 使得只需要一个线程负责 Selector 的监听，就可以实现上千万的客户端接入。

Selector 上可以注册多个 Channel 的多个事件，一个 Channel 也可以注册到多个 Selector 上。Channel 注册到 Selector 上会对应一个 SelectionKey，当 Channle 上有事件发生时可以通过 ```selectedKeys``` 获取到对应的 ```SelectionKey```，然后通过 ```SelectionKey``` 得到对应的 Channel，之后就可以通过 Channel 获取到数据：
```java

```

SelectionKey 表示 Selector 和 Channel 的注册关系，共有 4 中：
- OP_ACCEPT：有新的网络连接，16
- OP_CONNECT：连接已建立，8
- OP_READ：读操作，1
- OP_WRITE：写操作，4

#### Channel

Channel 本身不能存储数据，需要配合 Buffer 来完成数据传输。



Channel 是 NIO 的数据管道，网络数据通过 Channel 读取和写入，和流不同的是 Channel 是双工的，即 Channel 可以同时进行读写操作。

Channel 有 FileChannel、DatagramChannel、ServerSocketChannel、SocketChannel 等实现类：
- ```FileChannel```：用于文件的数据读写，通过文件流的 getChannel 方法将流转换成 Channel，或者直接调用静态方法 ```FileChannel#open``` 以 Channel 的方式读写文件
- ```DatagramChannel```：用于 udp 协议的数据读写
- ```ServerSocketChannel 和 SocketChannel```：用于 tcp 协议的数据读写





#### Buffer

Buffer 是一个存储特定基础类型数据的容器，其实质是特定基础类型元素的线性有限序列。Buffer  定义了三个基本属性：

- `capacity`：Buffer 的容量，在创建 Buffer 的时候需要指定 capacity，容量大小不为负数且一旦指定后就不允许修改，Buffer 中存储的元素数量不能超过 Buffer 的容量
- `limit`：Buffer 中第一个不能读取或者写入的元素的下标，即当前 Buffer 中存储的数据的最大长度，`limit` 的值不能为负数且永远不会大于 `capacity`，Buffer 初始化时 `limit` 初始化为 `capacity` 大小
- `position`：Buffer 中下一个将要被读取或者写入的元素的下标，`position` 不能为负数且永远不会大于 `limit`

```java
// 获取 buffer 的 position
buffer.position();
// 获取 buffer 的 limit
buffer.limit();
// 获取 buffer 的 capacity
buffer.capacity();
```

Buffer 是 NIO 双工模式中用于存储数据的载体，可以同时进行读写。Buffer 的每次读写操作使得 position 增加直到达到 limit，此后再次读取数据则会抛出 `BufferUnderflowException`，再次写入数据则会抛出 `BufferOverflowException`。

`Buffer` 提供 `mark` 属性表示 `reset` 方法调用后 position 重置的索引，`mark` 如果定义了则必须是正数且永远不会大于` position`，如果 `position` 或者 `limit` 重新调整为小于 `mark` 的值则会丢弃 `mark`，如果 `mark` 未定义则调用 `reset` 方法时会抛出 `InvalidMarkException`。

```java
// position 设置为 mark
// 如果 mark 未设置(-1) 则抛出异常
buffer.reset();

buffer.mark();
```

0 <= mark <= position <= limit <= capacity



```java
// limit 设置为 position
// position 设置为 0
// mark 设置为 -1
buffer.flip();

```









ByteBuffer 是 Buffer 常用的实现类，其有堆内分配和对外分配两种方式，使用 ```ByteBuffer#allocate``` 方法返回的是堆内分配的 ByteBuffer，使用 ```ByteBuffer#allocateDirect``` 方法返回的是堆外分配的 ByteBuffer。

#### File

##### Path

##### Files

#### Charset

### AIO

### 零拷贝

操作系统处理网络数据有两个阶段：等待数据和拷贝数据。等待数据是系统内核等待网卡接收到数据并写入到内核中，拷贝数据就是将内核中的数据拷贝到应用进程的空间中。

![网络数据处理]()

应用进程的每一次写操作都会把数据写到用户空间的缓冲区中，再由 CPU 将数据拷贝到系统内核的缓冲区中，之后再由 DMA 将数据拷贝到网卡中，最后由网卡发送出去。

应用程序的读操作则需要先由 DMA 将数据从网卡拷贝到内核缓冲区，然后由 CPU 将数据拷贝到用户空间的缓冲区，之后应用进程才能读取到数据。

应用进程的一次完整的读写操作需要在用户空间和内核空间进行数据拷贝，并需要 CPU 在用户空间和内核空间之间切换，通过零拷贝的方式可以减少数据的拷贝以及用户空间和内核空间的切换。

零拷贝(Zero-copy)技术就是取消用户空间和内核空间之间的数据拷贝，应用进程的读写操作如同直接写入内核空间一样从而无需进行数据拷贝。零拷贝有两种实现方式：mmap+write 方式和 sendfile 方式。





传统网络 IO 分析：
```java
read(file, tmp_buf, len)
write(socket, tmp_buf, len)
```
- 程序使用 read 方法，系统由用户态切换为内核态，磁盘中的数据由 DMA(Direct Memory Access) 方式读取到内核缓冲区(kernel buffer)，DMA 过程中不需要 CPU 参与，而是 DMA 处理器直接将硬盘的数据通过总线传输到内存中
- 系统由内核态切换为用户态，应用程序从内核缓冲区写入用户缓冲区，这个过程需要 CPU 参与
- 程序使用 write 方法，系统由用户态切换为内核态，数据从用户缓冲区写入到网络缓冲区(socket buffer)，这个过程需要 CPU 参与
- 系统由内核态切换到用户态，网络缓冲区的数据通过 DMA 的方式传输到网卡驱动中

内存映射(MMAP) 方式 IO 分析：
```java
tmp_buf = mmap(file, len);
write(socket, tmp_buf, len);
```
MMAP 原理是将用户缓冲区的内存地址和内核缓冲区的地址做一个映射，也就是说在用户态可以直接读取并操作内核空间数据。

- 程序使用 mmap 方法，系统由用户态切换到内核态，采用 DMA 方式将磁盘的数据读取到内核缓冲区
- 系统由内核态切换为用户态，由于用户缓冲区和内核缓冲区有映射，所以不需要消耗 CPU 将内核缓冲区数据拷贝到用户缓冲区
- 程序使用 write 方法，系统由用户态切换为内核态，需要使用 CPU 将内核缓冲区的数据拷贝到网络缓冲区中
- 系统由内核态切换为用户态，网络缓冲区的数据通过 DMA 方式传输到网卡驱动中

使用 MMAP 技术减少了一次数据拷贝，并没有减少上下文切换次数，在多线程操作同一块内存映射时需要采用并发编程的技术保证数据一致性。

sendfile 方式 IO 分析：
```java
snedfile(socket, file, len)
```
- sendfile 调用会引起系统由用户态切换到内核态，磁盘数据通过 DMA 方式读取到内核缓冲区
- 系统依然在内核态，内核缓冲区中的数据通过 CPU 拷贝到网络缓冲区
- 系统由内核态切换到用户态，并使用 DMA 将网络缓冲区的数据发送到网卡驱动


Java 中常用的零拷贝有 mmap（内存映射）和 sendFile。 mmap 通过内存映射，将文件映射到内核缓冲区，用户空间和内核空间可以共享内核空间的数据，减少用户空间到内核空间的数据拷贝(4 次减少到 3 次)。sendFile 是数据不经过用户态，直接从内核缓冲区进入到 SocketBuffer，在减少数据拷贝的同时也减少了状态上下文切换。

mmap 适合小数据量读写，sendFile 适合大文件传输；mmap 需要 4 次上下文切换，3 次数据拷贝，sendFile 需要 3 次上下文切换，最少 2 次数据拷贝；sendFile 可以利用 DMA 方式，减少 CPU 拷贝， mmap 则不能，必须从内核拷贝到 socket 缓冲区

**[Back](../)**