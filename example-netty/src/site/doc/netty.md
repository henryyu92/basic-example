## Netty
Netty 提供异步的、基于事件驱动的网络应用程序框架，用以开发高性能、高可靠的网络 IO 程序

Netty 基于 Reactor 模型，相对于阻塞式 IO 模型，Reactor 模型使用了 I/O 复用模型，多个连接公用一个阻塞对象，应用程序只需要在一个阻塞对象等待而无需阻塞等待所有连接。Reactor 模型还利用了线程池资源，不再为每个连接创建线程，将连接完成后的业务处理任务分配给线程进行处理，一个线程可以处理多个连接的任务。



#### Netty 模型


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


### 异步模型

Netty 的 IO 操作都是异步的，调用者不能立刻获得结果，而是通过 Future-Listener 机制主动获取结果或者通过通知机制获得 IO 操作结果。

Netty 的异步模型是建立在 future 和 callback 之上的，



