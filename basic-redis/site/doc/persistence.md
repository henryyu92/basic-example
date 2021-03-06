## 持久化

Redis 通过持久化机制保证数据不丢失，Redis 提供了 `RDB` 和 `AOF` 两种数据持久化的方式：

- `RDB`：以指定间隔将数据库在当前时间点的数据集以二进制文件(rdb)的形式持久化到磁盘
- `AOF`：以追加文件的形式将节点接收的写操作持久化到文件，在追加文件过大时会在后台进行重写

Redis 可以通过不同的配置可以实现不同的持久化机制：

- 不启用持久化，此时一旦节点异常就会丢失数据
- 只启用 `RDB` 持久化方式，有丢失部分数据的风险
- 只启用 `AOF` 持久化方式，通过重放记录的命令来恢复数据，相对较慢且生成的文件较大
- 同时启用 `RDB` 和 `AOF` 文件，尽可能保证数据不丢失

### RDB

RDB 通过将某个时间点的数据集持久化到磁盘形成 `dump.rdb` 文件从而实现数据的持久化，RDB 持久化可以通过执行 `BGSAVE` 命令执行也可以通过配置定期执行。

RDB 持久化方式的优势：

- RDB 生成的是结构紧凑的二进制文件，表示当前时间的数据集，这使得 rdb 文件非常适合作为备份
- RDB 持久化方式也非常适合做容灾恢复，将 RDB 持久化生成的二进制文件备份到安全的位置，在发生节点崩溃时直接加载 rdb 文件即可恢复当前的数据
- RDB 持久化是通过在后台 fork 子进程来生成二进制文件，因此主进程不会被阻塞也不会有磁盘 I/O
- 相较于 AOF 持久化方式，RDB 能够更快的重建数据

RDB 持久化方式的劣势：

- RDB 持久化通常间隔的时间较长，因此在间隔时间内写入的数据有可能会丢失
- RDB 持久化需要 fork 出子进程来执行持久化，如果数据量较大的话持久化操作时间会较长，从而会导致子进程会占用主进程的 CPU 资源

RDB 将数据集 dump 到磁盘形成 rdb 文件时需要执行三个步骤：

- 主进程 fork 出子进程
- 子进程将数据集写入到临时的 rdb 文件
- 子进程写完数据后替换成为新的 rdb 文件

Redis 可以通过设置使得节点定期执行 `BGSAVE` 命令将数据集持久化到磁盘，默认情况下持久化到磁盘的文件名为 `dump.rdb`。Redis 可以设置多个持久化条件，只要满足其中任意一个条件就会触发持久化：

```shell
# 设置文件名
dbfilename dump.rdb
# 900s 内至少有 1 次修改
save 900 1
# 300s 内至少有 10 次修改
save 300 10
# 60s 内至少有 10000 次修改
save 60 10000
```



### AOF 

AOF 持久化方式通过持久化服务器接收到的命令来持久化数据，并通过重放的形式恢复数据。Redis 支持多种 AOF 的持久化策略，通过设置不同的持久化策略可以在性能和数据一致性之间平衡。

AOF 持久化方式的优势：

- AOF 支持多种同步策略，默认每秒执行同步，当设置为每次写入都同步时可以保证数据不丢失。AOF 的同步是后台线程执行的，因此主线程不会受到太大影响
- AOF 是追加日志，因此即使掉电也不会损坏
- Redis 支持 AOF 日志的自动重写，当 AOF 文件过大时就会自动在后台将旧文件中的数据重写到新的文件中，重写的过程中服务器接收的命令依然追加到旧的文件中，一旦重写完成则会切换两个文件，之后的追加数据会在新的 AOF 文件中完成
- AOF 文件格式易于理解和解析，通过重放导出的 AOF 文件也可以快速重建数据

AOF 持久化方式的劣势：

- AOF 生成的文件是追加日志，因此文件通常比 RDB 文件大
- AOF 如果配置成每次写入则追加会比较慢

Redis 提供了多种 AOF 持久化策略，默认情况下 AOF 持久化方式并未开启，因此需要开启 AOF 持久化方式：

```shell
# 开启 AOF 持久化
appendonly yes
# 设置持久化策略
appendfsync everysec		# 每秒执行一次 fsync，默认策略
# appendfsync always		# 每次接收新命令都会执行 fsync
# appendfsync no			# 不执行 fsync
```

#### 日志重写

Redis 支持在 AOF 文件过大时自动在后台重建 AOF 文件而不会中断对外提供服务，Redis  AOF 文件崇高那些不需要对现有的 AOF 文件进行任务读取、分析或者写入操作，而是通过读取服务器当前的数据状态来实现。

AOF 文件重写的过程是先从数据库中遍历读取键现在的值，然后用一条命令去记录键值对代替之前记录的这个键值对的多条命令。

Redis AOF 文件重写是在子进程中完成的，这样可以同时达到两个目的：
- 子进程进行 AOF 重写期间，服务器进程(父进程)可以继续处理命令请求
- 子进程带有服务器进程的数据副本

子进程在进行 AOF 重写期间，服务器进程还需要继续处理命令请求，新的命令可能会对现有的数据库状态进行修改，从而使得服务器当前的数据库状态和重写后的 AOF 文件所保存的数据库转台不一致。Redis 服务器设置了一个 AOF 重写缓冲区用于解决这种数据不一致问题，这个缓冲区在服务器创建子进程之后开始使用，当 Redis 服务器执行完一个命令之后，它会同时将这个命令发送给 AOF 缓冲区和 AOF 重写缓冲区，这样可以保证：
- AOF 缓冲区的内容会定期被写入和同步到 AOF 文件，对现有 AOF 文件的处理工作会如常进行
- 从创建子进程开始，服务器执行的所有写命令都会被记录到 AOF 重写缓冲区中

当子进程完成 AOF 重写之后向父进程发送一个信号，父进程在接收到该信号后调用信号处理函数执行以下工作：
- 将 AOF 重写缓冲区中的所有内容写入到新 AOF 文件中，这时新 AOF 文件所保存的数据库状态将和服务器当前的数据库状态一致
- 对新的 AOF 文件进行改名，原子的覆盖现有的 AOF 文件，完成新旧两个 AOF 温家你的替换

父进程执行完信号处理函数之后就可以正常接受命令请求，在整个 AOF 重写过程中只有信号处理函数执行时会对服务器进程造成阻塞，在其他时候 AOF 重写都不会阻塞父进程，这将 AOF 重写对服务器性能造成的影响降到了最低。

**[Back](../)**