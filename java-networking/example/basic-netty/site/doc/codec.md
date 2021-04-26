## 编解码

数据在网络中传输的都是二进制字节码，发送数据前需要对数据进行编码，而接收到数据之后首先需要解码。Netty 实现了一系列使用的编解码器，它们都实现了 ChannelInboundHandler 或者 ChannelOutboundHandler，这些编解码器的 channelRead 方法被重写。

实际使用时，需要考虑跨语言问题，所以通常采用第三方的编解码器方案，如 Protobuf、hessian
### Protobuf



### 拆包和粘包

TCP 协议是基于字节流的协议，发送的分组数据不一定是一个完整的数据报，因此会发生拆包和粘包的问题，Netty 提供了对拆包和粘包问题的解决实现。



TCP/IP 是流协议，传输的是字节流，也就是说 TCP 底层并不了解上层业务数据的具体含义，它会根据 TCP 缓冲区的实际情况进行包的划分，所以一个完整的包可能会被 TCP 拆分成多个包进行发送，也有可能把多个小的包封装成一个大的数据包发送，这就是 TCP 的粘包和拆包问题。

#### TCP 粘包/拆包发生的原因

产生 TCP 粘包/拆包问题的原因有三个：

- 应用程序 write 的字节大小超出套接字接口发送的缓冲区大小
- 进行 MSS 大小的 TCP 分段
- 以太网帧的 payload 大于 MTU 进行 IP 分片

#### 粘包问题解决策略

由于底层的 TCP 无法理解上层的业务数据，所以在底层是无法保证数据包不被拆分和重组的，这个问题只能通过上层的应用协议栈设计解决。目前业界的主流协议解决方案有：

- 消息定长，如每个报文大小为固定长度 200 字节，如果不够空位补空格
- 在包尾增加回车换行符进行切割
- 将消息分为消息头和消息体，消息头中包含表示消息总长度的字段
- 自定义协议

#### LineBasedFrameDecoder

```java
public class TimeServer{
    public void bind(int port) throws Exception{
        // 配置服务端的 NIO 线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EvemtLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootStrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG, 1024)
            .childHandler(new ChildChannelHandler());
            // 绑定端口
            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        }finally{
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel>{
        protected void initChannel(SocketChannel channel) throws Exception{
            channel.pipeline().addLast(new LineBasedFrameDecoder(1024))
        }
    }
}
```

