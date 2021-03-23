## 网络编程

Java 支持基于 Socket 的网络编程，服务器端和客户端需要通过 Socket 建立连接，之后才能相互通信。Java 网络编程支持三种 I/O 模型：

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

```java
ServerSocket serverSocket = new ServerSocket(port);
// Acceptor 线程阻塞直到连接创建
Socket socket = serverSocket.accpet();
// 阻塞的获取数据
InputStream in = socket.getInputStream();
socket.close();
serverSocket.close();
```

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

```java
ServerSocketChannel serverChannel = ServerSocketChannel.open();
serverChannel.configureBlocking(false);
serverChannel.bind(new InetSocketAddress("localhost", 9999));

Selector selector = Selector.open();
// 注册 channel
selector.register(selector, SelectionKey.OP_ACCEPT);
// Acceptor 监听事件
while(selector.accpet()){
    
}
```



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

### AIO



**[Back](../)**