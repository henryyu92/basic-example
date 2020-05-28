## Netty
Netty 提供异步的、基于事件驱动的网络应用程序框架，用以开发高性能、高可靠的网络 IO 程序

Netty 基于 Reactor 模型，相对于阻塞式 IO 模型，Reactor 模型使用了 I/O 复用模型，多个连接公用一个阻塞对象，应用程序只需要在一个阻塞对象等待而无需阻塞等待所有连接。Reactor 模型还利用了线程池资源，不再为每个连接创建线程，将连接完成后的业务处理任务分配给线程进行处理，一个线程可以处理多个连接的任务。



#### Netty 线程模型


```
                                        Boss Group
        +-------------------------------------------------------------------------------+  
        |                              NioEventLoopGroup                                |
        |  +--------------------------------+      +--------------------------------+   |
        |  |         NioEventLoop           |      |         NioEventLoop           |   |
        |  |  +----------+    +----------+  |      |  +----------+    +----------+  |   |
        |  |  | Selector |--->| Acceptor |  |      |  | Selector |--->| Acceptor |  |   |
        |  |  +----|-----+    +----------+  |      |  +----|-----+    +----------+  |   |
        |  +-------|------------------------+      +-------|------------------------+   |
        |          |                                       |                            |
        +----------|---------------------------------------|----------------------------+ 
                   |                                            
                   | SocketChannel -->  NIOSocketChannel
                   |
                   | register          Worker Group
       +--------------------------------------------------------------------------+
       |                             NioEventLoopGroup
       |  +--------------------------------+
       |  |         NioEventLoop           |
       |  |  +----------+    +----------+  |
       |  |  | Selector |--->| Acceptor |  |
       |  |  +----|-----+    +----------+  |
       |  |  +----|-----+    +----------+  |
       |  +-------|------------------------+
       +----------|----------------------------------------------------------------+

         Pipeline
         +-----------------+
```

Boss Group 维护 Selector 只关注 Accept 事件，当接收到 Accept 事件获取到对应的 SocketChannel 封装成 NIOSocketChannel 并注册到 Workder，Worker 线程监听到 Channel 的事件后就分发到 Handler 处理


Netty 抽象出 BossGroup 专门负责接收客户端的连接，WorkerGroup 专门负责网络的读写；BossGroup 和 WorkerGroup 类型都是 NioEventLoopGroup，这个 LoopGroup 中含有多个事件循环，每个事件循环是 NioEventLoop；NioEventLoop 表示一个不断循环的执行处理任务的线程，每个 NioEventLoop 都有一个 Selector 用于监听绑定的 Soceket；NioEventLoopGroup 可以有多个线程，即可以包含有多个 NioEventLoop

每个 Boss NioEventLoop 轮询 accept 事件，与 Client 建立连接生成 NioSocketChannel，并将其注册到某个 Workder NioEventLoop 上的 Selector；然后处理任务队列上的任务

每个 Worker NioEventLoop 轮询 read/write 事件，在事件对应的 NioSocketChannel 处理事件逻辑，然后处理任务队列上的任务；每个 Worker NioEventLoop 处理业务时会使用 PipeLine 处理，pipeline 包含了 channel，即通过 pipeline 可以获取到 channel，pipeline 中包含了很多 haneler 处理 channel 中的数据。


Netty 抽象出两组线程池，Boss Group 专门负责接收客户端连接，Worker Group 专门负责网络读写操作。NioEventLoop 表示一个不断循环执行处理任务的线程，每个 NioEventLoop 都有一个 selector，用于监听绑定在其上的 socket 网络通道；NioEventLoop 内部采用串行化设计，从消息的读取->解码->处理->编码->发送，始终由 IO 线程 NioEventLoop 负责

- 每个 NioEventLoop 下包含多个 NioEventLoop
- 每个 NioEventLoop 中包含一个 Selector，一个 taskQueue
- 每个 NioEventLoop 的 Selector 可以注册监听多个 NioChannel
- 每个 NioChannel 只会绑定在唯一的 NioEventLoop 上
- 每个 NioChannel 都绑定有一个自己的 ChannelPipeline


Netty 抽象出两组线程池 BossGroup 专门负责接收客户端的连接，WorkGroup 专门负责网络的读写。BossGroup 和 WorkerGroup 类型都是 NioEventLoopGroup，NioEventLoopGroup 相当于一个事件循环组，这个组中含有多个事件循环，每个事件循环是 NioEventLoop。NioEventLoop 表示一个不断循环的执行处理任务的线程，每个NioEventLoop 都有一个 Selector 用于监听绑定在其上的 socket 的网络通信。NioEventLoopGroup 可以由多个事件循环，每个 Boss NioEventLoop 事件循环执行三个步骤：轮询 accept 事件，处理 accept 事件与 client 建立连接生成 NioSocketChannel 并将其注册到 Worker NioEventLoop 的 Selector 上，处理任务队列上的任务(runAllTask)。Worker NioEventLoop 事件循环执行三个步骤：轮询 read/write 事件，处理 io 事件在对应的 NioSocketChannel 上处理，处理任务队列上的任务(runAllTask)。

每个 Worker NioEventLoop 处理业务时会使用 pipeline，pipeline 中包含了 channel


### 异步模型

当业务处理非常耗时，可以将业务处理提交到 channel 对应的 NioEventLoop 中异步处理。
```java
ctx.channel().execute(()->{Sytem.out::println})
```

Netty 的 IO 操作都是异步的，包括 bind, write, connect 等操作会简单的返回一个 ChannelFuture，调用者不能立刻获得结果，而是通过 Future-Listener 机制主动获取结果或者通过通知机制获得 IO 操作结果。

Netty 的异步模型是建立在 future 和 callback 之上的，callback 就是回调。

#### ChnnelFuture

ChannelFuture 表示 Netty 处理的结果，可以对 ChannelFuture 添加监听器，当事件发生是会执行监听器的回调函数


#### Selector

Netty 基于 Selector 对象实现 IO 多路复用，通过 Selector 一个线程可以监听多个连接的 Channel 事件。当向一个 Selector 中注册 Channel 后，Selector 内部的机制就可以自动不断的查询这些注册的 Channel 是否有已就绪的 IO 事件，这样就可以使用一个线程管理多个 Channel。

#### ChannelHandlerContext

保存 Channel 相关的所有上下文信息，同时关联一个 ChannelHandler 对象，ChannelHandlerContext 中包含了一个具体的事件处理器 ChannelHandler，同时 ChannelHandlerContext 中也绑定了对应的 pipeline 和 Channel 的信息，方便对 ChannelHandler 进行处理。

#### ChannelOption

Netty 在创建 Channel 后，一般都要设置 ChannelOption 参数，ChannelOption 参数设置了网络相关的参数。

ChannelOption.SO_BACKLOG：对应 TCP/IP 协议 listen 函数中的 backlog 参数，用来初始化服务器可连接队列大小，服务器端处理客户端连接请求是顺序处理的，所以同一时间只能处理一个客户端连接，多个客户端来的时候，服务器端将不能处理的客户端连接请求放在队列中等待处理，backlog 参数指定了队列大小

ChannelOption.SO_KEEPALIVE：一直保持连接活动状态

#### EventLoopGroup 和 NioEventLoopGroup

EventLoopGroup 是一组 EventLoop 的抽象，Netty 为了更好的利用多核 CPU 资源，一般会有多个 EventLoop 同时工作，每个 EventLoop 维护着一个 Selector 实例。
 
EventLoopGroup 提供 next 接口，可以从组里面按照一定规则获取其中一个 EventLoop 来处理任务，在 Netty 服务器端编程中，一般需要提供两个 EventLoopGroup
一个服务器端口即一个 ServerSocketChannel 对应一个 Selector 和一个 EventLoop 线程，BossEventLoop 负责接收客户端的连接并将 SocketChannel 交给 WorkerEventLoop 来进行 IO 处理。

BossEventLoopGroup 通常是一个单线程的 EventLoop，维护着一个注册了 ServerSocketChannel 的 Selector 实例，BossEventLoop 不断轮询 Selector 将连接时间分离出来

BossEventLoop 将 OP_ACCEPT 事件分离出来后将接收到的 SocketChannel 注册到 WorkerEventLoopGroup，WorkerEventLoopGroup 由 next 方法选择出一个 EventLoop 来将这个 SocketChannel 注册到其维护的 Selector 并对其后的 IO 事件进行处理。


#### Unpooled

Netty 提供了一个专门用来操作缓冲区的工具类