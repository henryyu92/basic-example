## 编解码

数据在网络中传输的都是二进制字节码，发送数据前需要对数据进行编码，而接收到数据之后首先需要解码。Netty 实现了一系列使用的编解码器，它们都实现了 ChannelInboundHandler 或者 ChannelOutboundHandler，这些编解码器的 channelRead 方法被重写。

实际使用时，需要考虑跨语言问题，所以通常采用第三方的编解码器方案，如 Protobuf、hessian
### Protobuf



### 拆包和粘包

TCP 协议是基于字节流的协议，发送的分组数据不一定是一个完整的数据报，因此会发生拆包和粘包的问题，Netty 提供了对拆包和粘包问题的解决实现。

