## 过期

正常情况下，在没有显式删除一个已经创建的 key 之前，该 key 会永远存在。EXPIRE 系列命令只需要占用 key 的一些额外内存，就可以实现当某个 key 设置了过期，Redis 保证当过期时间到达就会删除这个 key。

Redis 有四个不同的命令可以用于设置键的生存时间或过期时间：
- ```EXPIRE <key> <ttl>```：将 key 的生存时间设置为 ttl 秒
- ```PEXPIRE <key> <ttl>```：将 key 的生存时间设置为 ttl 毫秒
- ```EXPIREAT <key> <timestamp>```：将 key 的过期时间设置为 timestamp 所指定的秒数时间戳
- ```PEXPIREAT <key> <timestamp>```：将 key 的过期时间设置为 timestamp 所制定的毫秒数时间戳

实际上 EXPIRE, PEXPIRE 和 EXPIREAT 三个命令都是 PEXPIREAT 命令实现：
```c
// EXPIRE 命令转换为 PEXPIRE 命令
def EXPIRE(key, ttl_in_sec):
  ttl_in_ms = sec_to_ms(ttl_in_sec)
  PEXPIRE(key, ttl_in_ms)

// PEXPIRE 命令转换为 PEXPIREAT 命令
def PEXPIRE(key, ttl_in_ms):
  now_ms = get_current_unix_timestamp_in_ms()
  PEXPIREAT(key, now_ms + ttl_in_ms)

// EXPIREAT 命令转换为 PEXPIREAT 命令
def EXPIREAT(key, expire_time_in_sec):
  expire_time_in_ms = sec_to_ms(expire_time_in_sec)
  PEXPIREAT(key, expire_time_in_ms)
```
过期设置只有在删除 key 和修改 key 内容的情况下才会失效。如果使用 rename 重命名，那么过期设置将会转移到新的 key，使用 ttl 命令可以查看过期剩余时间。使用 PERSIST 命令也可以移除键的过期时间，使用 PERSIST 命令可以将键值对持久化。
```
set k v px 20
ttl k
```
### 过期策略

Redis 过期有两种方式：passive 和 active
- passive 方式是当 client 尝试访问过期的 key 的时候才真正过期。这种方式的问题在于如果没有被访问，则过期的 key 将永远不会真正的过期
- active 方式会定期随机测试几个已经设置了过期的 key 并且删除已经过期的 key，具体为每秒执行 10 次操作：随机测试 20 个 key，删除已经过期的 key，如果超过 25% 的 key 已经过期则重复操作

Redis key 的过期信息是以绝对的 Unix 时间戳的方式存储的，这意味着即使 Redis 实例没有运行，时间也会一直流逝。因此如果想要 expire 效果良好，计算机的时间必须稳定。如果在两个机器间移动 RDB 文件并在各自的时钟内 desync，可能会发生有趣的事(例如所有的 key 在加载的时候就已经过期了)。

### RDB 中的过期
在执行 SAVE 命令或者 BGSAVE 命令创建一个新的 RDB 文件时，程序会对数据库中的键进行检查，已过期的键不会被保存到新创建的 RDB 文件中。

在启动 Redis 服务器时，如果服务器开启了 RDB 功能，将对 RDB 文件进行载入：
- 如果服务器以主服务器模式(master)运行，那么在载入 RDB 文件时程序会对文件中保存的键进行检查，未过期的键会被载入到数据库中，过期的键会被忽略
- 如果服务器以从服务器模式(slave)运行，那么在载入 RDB 文件时文件中保存的键都会被载入到数据库中，在主从服务器数据同步的时候从服务器的数据会被清空，因此载入过期的键不会造成影响

### AOF 中的过期键

当服务器以 AOF 模式运行时，如果数据库中的某个键已经过期但还没有被惰性删除或者定期删除，那么 AOF 文件不会因为这个过期键而产生影响。当 key 到期时会在 AOF 文件中合成 DEL 操作记录该键已经被删除，这样在 master 中不存在一致性的错误。但是副本不能独立的过期 key，所以在等待 AOF 文件中 DEL 操作前并没有过期，当副本被选举为 master 后，就能和 master 独立的过期 key 了。

### 复制中的过期键
当服务器运行在复制模式(replica)下时，从服务器的过期键删除动作由主服务器控制：
- 主服务器(master)在删除一个过期键之后会显式地向所有从服务器(slave)发送一个 DEL 命令，告知从服务器删除这个过期键
- 从服务器在执行客户端发送的读请求时，即使碰到过期键也不会将过期键删除，而是继续像处理未过期的键一样来处理过期键
- 从服务器只有在接收到主服务器发来的 DEL 命令之后，才会删除过期键

### 雪崩、穿透和击穿

雪崩是指缓存中大批热点数据过期后系统涌入大量查询请求，导致请求渗透到数据库导致数据库压力过大。

穿透是指大量请求未能命中缓存直接请求到数据库导致数据库压力过大。

击穿是指缓存中某些热点数据失效瞬间大量请求击穿了缓存，直接请求到数据库导致数据库压力过大。

- 为了避免缓存雪崩，在 Redis 中缓存数据的过期时间需要随机，且为了避免由于 Redis 异常导致缓存不可用需要使用 Redis 集群，以及业务需要设置限流和降级处理。
- 为了避免缓存穿透需要在 Redis 中为这种查询缓存默认值，这样请求就不会到数据库。
- 为了避免缓存击穿，可以设置缓存不过期，或者在查询前随机自旋一段时间使得缓存能够重新构建

**[Back](../)**