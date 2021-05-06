### Pipeline

Redis 是使用 client-server 模型和 Request/Response 协议的 TCP 服务器，这意味这每次请求都会有以下步骤：

- 客户端向服务器发送查询，并通常以阻塞的方式从 Socket 中读取服务器的响应
- 服务器处理该命令并将响应发送回客户端

客户端和服务器是通过网络来连接的，这种连接可能非常快也可能非常慢，但是无论是何种连接，请求数据包从客户端到服务器然后响应数据包从服务器返回到客户端都是需要时间的，这个时间就成为 RTT(Round Trip Time)。

当客户端需要连续执行多个请求时就会严重影响 Redis 的吞吐量，Redis 使用 Pineline 技术在客户端还没有读取响应数据就处理下一个请求来提升服务器的吞吐量，这样就可以将多个命令发送到服务器而无需等待回复，最后只需一步即可读取全部响应。

> 客户端使用 Pipelining 发送命令的时候，服务器将强制使用内存对请求的结果进行排队，因此如果需要使用 Pipelining 发送大量的命令，需要设置一个比较合理的批次从而防止占用太多内存。

Pipelining 技术不仅解决了 RTT 耗时，而且也大大提高了 Redis 服务器每秒可执行的操作量。这是因为，在不使用 Pipelining 的情况下，Socket I/O 操作是非常昂贵的，因为涉及到 read() 和 write() 系统调用，也就意味着用户进程和内核进程的切换，这个切换在速度上代价非常大。使用 Pipelining 操作，使用单个 read() 系统调用执行多个请求命令，并使用单个 write() 系统调用返回多个响应。因此每秒执行的总查询数会随着 Pipelining 的长度线性增加并最终达到不使用 Pipelining 的 10倍。



### Pub/Sub

Redis 提供了发布订阅模式，客户端可以订阅一个或者多个通道，并且在其他客户端向通道发送消息时被通知。Redis 提供了 6 个发布订阅相关的命令：

- `PSUBSCRIBE pattern [pattern ...]`：订阅指定模式的通道，支持 `?`、`*` 通配符，时间复杂度为 O(N)
- `PUNSBUSCRIBE [pattern ...]`：取消订阅指定模式的通道，如果没有指定模式则取消所有已经订阅的通道，时间复杂度为 O(N+M)，其中 N 是订阅的数量，M 为所有被订阅的数量
- `SUBSCRIBE channel [channel ...]`：客户端订阅指定的通道，一旦客户端进入订阅状态就不能发出除 `SUBSCRIBE`、`PSUBSCRIBE`、`UNSUBSCRIBE`、`PUNSBUSCRIBE`、`PING`、`RESET` 和 `QUIT` 命令外的其他命令，时间复杂度为 O(N)
- `UNSUBSCRIBE [channel ...]`：取消订阅指定的通道，如果没有指定则取消订阅所有的通道，时间复杂度为 O(N)

### LRU 缓存

设置 maxmemory 可以限制 Redis 使用的最大内存，通过 redis.conf 配置，如 maxmemory 100mb。maxmemory 设置为 0 表示不限制内存，这是 64bit 操作系统的默认设置，而 32bit 系统隐式的设置为 3GB。
当 Redis 使用的内存达到 maxmemory 设置的大小时，Redis 可以根据 maxmemory-policy 配置选取不同的策略：

- noeviction - 当达到内存限制且客户端尝试执行导致使用更多内存的命令是返回错误
- allkeys-lru - 尝试先删除最近最少使用(LRU, Less Recently Use)的 key，以便为添加新数据腾出空间
- volatile-lru - 尝试先删除最近最少使用的 key，但是仅限于设置了过期的 key
- allkeys-random - 随机的删除一些 key
- volatile-random - 随机删除一些已经设置了过期的 key
- volatile-ttl - 删除设置了过期的 key，但是尝试删除较短生存时间(TTL, Timt To Live)的 key
  推荐使用：
- 当请求的分布服从幂律分布时，使用 allkeys-lru，也就是说请求的那部分子集的访问频率远远大于其他；如果不确定的话这是一个好的选择。
- 如果有需要循环访问所有 key 的情况或者请求 key 的频率分布均匀，使用 allkeys-random
- 如果在创建缓存的时候设置了不同的过期时间，则使用 volatile-ttl