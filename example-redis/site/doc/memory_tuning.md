### Redis 内存优化

### Redis 过期
正常情况下，在没有显式删除一个已经创建的 key 之前，该 key 会永远存在。EXPIRE 系列命令可以将 key 和过期联系起来，只需要占用 key 的一些额外内存，就可以实现当某个 key 设置了过期，Redis 保证当过期时间到达就会删除这个 key。

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

// todo

Redis 过期有两种方式：passive 和 active
- passive 方式是当 client 尝试访问过期的 key 的时候才真正过期。这种方式的问题在于如果没有被访问，则过期的 key 将永远不会真正的过期
- active 方式会定期随机测试几个已经设置了过期的 key 并且删除已经过期的 key，具体为每秒执行 10 次操作：随机测试 20 个 key，删除已经过期的 key，如果超过 25% 的 key 已经过期则重复操作

Redis key 的过期信息是以绝对的 Unix 时间戳的方式存储的，这意味着即使 Redis 实例没有运行，时间也会一直流逝。因此如果想要 expire 效果良好，计算机的时间必须稳定。如果在两个机器间移动 RDB 文件并在各自的时钟内 desync，可能会发生有趣的事(例如所有的 key 在加载的时候就已经过期了)。

#### RDB 中的过期键
在执行 SAVE 命令或者 BGSAVE 命令创建一个新的 RDB 文件时，程序会对数据库中的键进行检查，已过期的键不会被保存到新创建的 RDB 文件中。

在启动 Redis 服务器时，如果服务器开启了 RDB 功能，将对 RDB 文件进行载入：
- 如果服务器以主服务器模式(master)运行，那么在载入 RDB 文件时程序会对文件中保存的键进行检查，未过期的键会被载入到数据库中，过期的键会被忽略
- 如果服务器以从服务器模式(slave)运行，那么在载入 RDB 文件时文件中保存的键都会被载入到数据库中，在主从服务器数据同步的时候从服务器的数据会被清空，因此载入过期的键不会造成影响
#### AOF 中的过期键
当服务器以 AOF 模式运行时，如果数据库中的某个键已经过期但还没有被惰性删除或者定期删除，那么 AOF 文件不会因为这个过期键而产生影响。当 key 到期时会在 AOF 文件中合成 DEL 操作记录该键已经被删除，这样在 master 中不存在一致性的错误。但是副本不能独立的过期 key，所以在等待 AOF 文件中 DEL 操作前并没有过期，当副本被选举为 master 后，就能和 master 独立的过期 key 了。
#### 复制中的过期键
当服务器运行在复制模式(replica)下时，从服务器的过期键删除动作由主服务器控制：
- 主服务器(master)在删除一个过期键之后会显式地向所有从服务器(slave)发送一个 DEL 命令，告知从服务器删除这个过期键
- 从服务器在执行客户端发送的读请求时，即使碰到过期键也不会将过期键删除，而是继续像处理未过期的键一样来处理过期键
- 从服务器只有在接收到主服务器发来的 DEL 命令之后，才会删除过期键

### Reids LRU 缓存
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

**[Back](../)**