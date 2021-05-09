## Replication

Replication 的思想使将数据在集群的多个节点同步、备份以提高集群数据的可用性。MySQL 的 Replication 架构通常由一个 master 和多个 slave 构成，master 接收应用的 write 操作(事务操作)，slave 接收 read 操作，master 上发生的数据变更都会复制给 slave。

Replication 有如下优点：
- 扩展：将负载分布在多个 slave 上以提高性能，所有的事务操作都由 master 处理，其他的 read 转发给 slave 操作，这样对于读写比较高的应用可以通过增加 slave 节点提高并发能力，而事务操作都是在 master 上处理的，所有对提升写性能并不明显
- 数据安全：slave 可以中断自己的 replication 进程而不会影响 master，所以可以在 slave 上运行全量 backup，如果在 master 上 backup 会使 master 处于 readonly 状态影响 master 的写性能
- 分析：数据在 master 上创建，可以在 slave 上分析这些数据而不会影响 master 的性能，也可以将 slave 作为数据源同步到其他数据平台进行数据处理

MySQL 主从复制涉及到三个线程，一个运行在主节点(log dump thread)，其余两个(I/O thread， sql thread)运行在从节点：

- `binary log dump` 线程：当从节点连接主节点时，主节点会创建 `log dump` 线程用于发送 `bin log` 的内容，在读取的时候需要加锁，读取完之后会释放锁
- `io` 线程：当从节点执行 `start slave` 命令后，从节点创建 i/o 线程来连接主节点并请求主节点中更新的 bin log ，并将接收到的 bin log 更新保存到 本地的 relay log 中
- `sql` 线程：负责读取 relay log 中的内容，解析成具体的操作并执行，最终保证主从数据的一致性

每个主从连接都需要三个线程来完成，当主节点有多个从节点时，主节点会为每个从节点创建 `binary log dump` 线程，而每个从节点会创建 io 线程和 sql 线程。从节点将 io 线程和 sql 线程独立使得从主节点获取数据和解析执行操作相互独立而不会互相影响。

MySQL 主从复制的流程如下：

- 从节点的 IO 线程连接主节点，并请求从指定日志文件的指定位置之后的日志内容
- 主节点收到从节点的请求后将指定的日志信息返回，同时还会返回本次的 bin log 位置
- 从节点的 sql 线程检测到 relay log 中新增了内容后会将 relay log 的内容解析成主节点实际执行过的操作，并在本数据库中执行

MySQL 主从复制默认是异步的，主节点在写完 binlog 就返回成功，而不需要数据同步到从节点。异步模式，无法保证当master失效后所有的updates已经复制到了slaves上，只有重启master才能继续恢复这些数据，如果master因为宿主机器物理损坏而无法修复，那些尚未复制到slaves上的updates将永久性丢失；因此异步方式存在一定的数据丢失的风险，但它的优点就是master支持的write并发能力较强，因为master上的writes操作与slaves的复制是互为独立的。

异步复制使得从节点上的数据总是有一定的延后，但是这种延迟非常短暂。


https://www.cnblogs.com/nulige/p/9491850.html