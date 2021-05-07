## 过期

Redis 允许为每个 key 设置生存时间，当 key 过期时会自动从节点上删除。key 的过期由 `EXPIRE` 系列命令设置，这些命令可以用于设置 key 的生存时间或者过期时间：

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
设置的过期时间只有通过使用删除或者覆盖 key 对应 value 的命令才会被清除，包括 `del`、`set`、`getset` 等。修改 key 对应的 value 是不会影响过期时间，也就是说 `incr`、`lpush`、`hset` 等只修改 value 但是并不会覆盖的命令不会改变 key 对应的过期时间。

使用 `psersist` 命令会将 key 持久化，此时 key 的过期时间会被清除。

`rename` 命令会将过期时间从旧的 key 转移到新的 key，即使新的 key 已经存在

如果使用 `expire/pexpire` 的值为负数，或者 `expireat/pexpireat` 的值为过去的时间则会立即删除 key 而不是使 key 过期。

对已经设置了过期时间的 key 使用 `expire` 命令会更新该 key 的过期时间。

使用 `ttl` 命令可以查看剩余的生存时间

```
set k v px 20
ttl k
```
Redis 中的过期时间是以绝对的 Unix 时间戳的方式存储，这意味着即使 Redis 实例没有运行，时间也会流逝。因此如果使用 rdb 文件在两台时钟不一致的机器间迁移数据，会导致数据在读取之后立即过期。

### 过期策略

Redis 的过期方式有 passive 和 active 两种：

- passive 方式是当 client 尝试访问过期的 key 的时候才真正过期。这种方式的问题在于如果没有被访问，则过期的 key 将永远不会真正的过期
- active 方式会定期随机测试几个已经设置了过期的 key 并且删除已经过期的 key，具体为每秒执行 10 次操作：随机测试 20 个 key，删除已经过期的 key，如果超过 25% 的 key 已经过期则重复操作

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

- 雪崩是指缓存中大批热点数据过期后系统的请求渗透到数据库导致数据库压力过大。避免缓存雪崩可以在缓存数据时为 key 的过期时间设置增加随机值
- 缓存穿透是请求没有命中缓存并且数据库中也不存在数据，这种情况是请求参数不当导致，应该在请求执行前进行参数校验
- 缓存击穿是某些热点数据过期导致瞬间大量请求到达数据库导致数据库压力较大，这种情况可以在查询不到数据时自旋一段时间使得缓存能够重新构建，此外还可以使用后台线程在数据过期前刷新 value 使的过期时间更新

### 缓存一致性

缓存一致性指的是在数据库和缓存都存在数据的情况下，如果需要更新数据如何保证缓存和数据库的数据一致。

数据库的更新和缓存的更新不是原子操作，通常为了保证数据的安全性会先更新数据库，然后在更新缓存。

- 如果更新数据库后更新缓存失败则可以将更新动作通过后台线程重试或者通过队列的方式更新缓存使得达到最终一致性
- 如果需要保证强一致性，则需要使用到分布式锁以及分布式事务，这样处理性能开销很大，使用缓存的意义不大

**[Back](../)**