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



