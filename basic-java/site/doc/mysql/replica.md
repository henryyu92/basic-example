## Replication

Replication 的思想使将数据在集群的多个节点同步、备份以提高集群数据的可用性。MySQL 的 Replication 架构通常由一个 master 和多个 slave 构成，master 接收应用的 write 操作(事务操作)，slave 接收 read 操作，master 上发生的数据变更都会复制给 slave。

Replication 有如下优点：
- 扩展：将负载分布在多个 slave 上以提高性能，所有的事务操作都由 master 处理，其他的 read 转发给 slave 操作，这样对于读写比较高的应用可以通过增加 slave 节点提高并发能力，而事务操作都是在 master 上处理的，所有对提升写性能并不明显
- 数据安全：slave 可以中断自己的 replication 进行而不会影响 master，所以可以在 slave 上运行全量 backup，如果在 master 上 backup 会使 master 处于 readonly 状态影响 master 的写性能
- 分析：数据在 master 上创建，可以在 slave 上分析这些数据而不会影响 master 的性能，也可以将 slave 作为数据源同步到其他数据平台进行数据处理

Replication 模式中，在 master 上发生的数据变更都将被立即写入 binlog，此后被 slave 读取到本地并应用这些数据变更操作从而实现复制。slave 的数据同步只会消耗较少的 master 资源，通常一个 master 组合几个 slave。




https://www.cnblogs.com/nulige/p/9491850.html