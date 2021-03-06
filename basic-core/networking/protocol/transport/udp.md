## UDP 协议



`UPD` 协议的特点：

- 无连接，数据发送前无需建立连接，减少了开销和数据发送时延
- 不可靠，数据发送后不需要响应确认消息，不能保证消息可靠到达，减少了状态控制的开销
- 面向报文，不对应用程序的报文进行拆分和合并，也不对网络层的用户数据报进行拆分和合并。应用程序必须选择合适大小的报文，太大则会导致网络层传输时的分片从而降低传输效率，太小则会导致网络层传输的有效数据比例过低从而降低传输效率
- 没有拥塞控制，在出现网络拥塞时不会降低源主机的发送速率，但是在发生网络拥塞时数据传输会丢失数据
- 支持一对一、一对多、多对多的交互通信
- 首部只有 8 个字节，开销小