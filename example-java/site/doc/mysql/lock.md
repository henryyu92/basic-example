### 锁
InnoDB 存储引擎实现了两种标准的行级锁：
- 共享锁(S Lock)：允许事务读一行数据
- 排他锁(X Lock)：允许事务删除或更新一行数据

如果一个事务 T1 已经获取了行 r 的共享锁，那么另外的事务 T2 可以立即获得行 r 的共享锁，因为读取并没有改变行 r 的数据，这种情况称为锁兼容(Lock Compatible)；但是事务 T3 想获取行 r 的排他锁，则必须等待事务 T1、T2 释放行 r 上的共享锁，这种情况称为锁不兼容。

-|X|S
-|-|-
X|不兼容|不兼容
S|不兼容|兼容

InnoDB 支持事务在行级上的锁和表级上的锁同时存在，为了支持不同粒度的加锁操作 InnoDB 存储引擎支持额外的锁方式，称为意向锁(Intention Lock)。InnoDB 支持两种意向锁：
- 意向共享锁(IS Lock)：事务可以获取一张表中某几行的共享锁
- 意向排他锁(IX Lock)：事务可以获取一张表中某几行的排他锁

意向锁不会阻塞除全表扫描外的任何请求：

-|IS|IX|S|X
-|-|-|-|-
IS|兼容|兼容|兼容|不兼容
IX|兼容|兼容|不兼容|不兼容
S|兼容|不兼容|兼容|不兼容
X|不兼容|不兼容|不兼容|不兼容

通过 ```show engine innodb status``` 命令查看当前锁请求信息：
```sql
show engine innodb status;
```
information_schema 数据库的 INNODB_TRX 表记录当前数据库中事务的情况： 
```
select * from information_schema.INNODB_TRX;
```
表结构包含多个字段：
- ```trx_id```：InnoDB 存储引擎内部唯一的事务ID
- ```trx_state```：当前事务的状态
- ```trx_started```：事务的开始时间
- ```trx_requested_lock_id```：当前事务等待事务的锁ID，如果 trx_state 为 LOCK WAIT 则代表当前事务等待之前事务占用锁资源的 id 否则为 null
- ```trx_wait_started```：事务等待开始时间  
- ```trx_weight```：事务的权重，反映一个事务修改和锁住的行数；在 InnoDB 存储引擎中，当发生死锁需要回滚时，InnoDB 存储引擎会选择该值最小的事务进行回滚  
- ```trx_mysql_thread_id```：MySQL 中的线程 ID， show processlist 显示的结果  
- ```trx_query```：事务运行的 SQL

INNODB_LOCKS 表记录当前数据库中锁的情况  
```
select * from information_schema.INNODB_LOCKS;
```
表结构包含多个字段：
- ```lock_id```：锁的 ID  
- ```lock_tx_id```：事务的 ID  
- ```lock_mode```：锁的模式  
- ```lock_type```：锁的类型，表锁还是行锁  
- ```lock_table```：加锁的表  
- ```lock_index```：锁住的索引  
- ```lock_space```：锁对象的 space id  
- ```lock_page```：事务锁定页的数量，若是表锁则是 NULL  
- ```lock_rec```：事务锁定行的数量，若是表锁则是 NULL  
- ```lock_data```：事务锁定记录的主键值，若是表锁则是 NULL

INNODB_LOCK_WAITS 记录当前事务的等待  
```
select * from information_schema.INNODB_LOCK_WAITS;
```
表结构包含多个字段：
- requesting_trx_id：申请锁资源的事务 ID  
- requesting_lock_id：申请的锁的 ID  
- blocking_trx_id：阻塞的事务的 ID  
- blocking_lock_id：阻塞的锁的 ID

#### 一致性非锁定读
一致性非锁定读(consistent non-locking read)指的是 InnoDB 存储引擎通过行多版本控制(multi versioning)的方式来读取当前执行时间数据库中行的数据；如果读取的行正在执行 DELETE 或 UPDATE 操作，这时读操作不会等待行上的锁释放，而是读取行的一个快照数据。

之所以称为非锁定读，是因为不需要等待访问的行上 X 锁的释放。快照数据指的是该行的之前版本的数据，该实现是通过 undo 段来完成，读取快照数据是不需要上锁的(没有事务会修改历史数据)，因此没有额外的锁开销；从而提高了数据库的并发性。

快照数据其实就是当前行数据之前的历史版本，每行记录可能有多个版本，也就是说一行的记录可能有多个快照数据。这种技术称为多版本技术，对其进行并发控制的技术称为多版本并发控制(Multi Version Concurrency Control, MVCC)。

在事务隔离级别 Read Committed 和 Repeatable read 下，InnoDB 存储引擎使用非锁定的一致性读。

对于快照的读取，事务的不同隔离级别会有不同的策略：在 Read committed 事务隔离级别下，非一致性读总是读取被锁定行的最新一份快照数据，因此第二次读取的时候不能读取到值；在 Repeatable read 事务隔离(MySQL 默认隔离了级别)级别下，非一致性读总是读取事务开始时的行数据版本，因此第二次读取的时候依然能读取到值，从而避免了不可重复读的问题。

时间|会话 1|会话2
-|-|-
1|BEGIN;|
2|SELECT * FROM parent WHERE id=1|
3||BEGIN;
4||UPDATE parent set id=3 WHERE id=1;
5|SELECT * FROM parent WHERE id=1;|
6||COMMIT;
7|SELECT * FROM parent WHERE id=1;|
8|COMMIT;|

会话 1 开启事务并未提交，此时会话 2 开启事务并执行 update 语句在 id=1 的行上添加 X 锁，此时会话 1 在 id=1 的行执行 select 操作，根据 InnoDB 存储引擎特性该操作使用非锁定一致性读，此时会话 1 不会等待会话 2 释放锁而直接读取快照数据；当会话 2 执行 COMMIT 操作提交事务之后由于数据有修改此时读取的数据还是事务开始时的行数据。

#### 一致性锁定读(locking read)
InnoDB 提供了对数据库的读操作显示加锁的支持，即对 SELECT 语句显示的加锁。
- ```SELECT ... FOR UPDATE```：对读取的行记录加一个 X 锁，其他事务不能对已经锁定的行上加任何锁
- ```SELECT ... LOCK IN SHARE MODE```：对读取的行记录加一个 S 锁，其他事务可以向被锁定的行加 S 锁，但是如果加 X 锁，则会被阻塞

**对于一致性非锁定读，即使是读取的行已经执行了 SELECT ... FOR UPDATE 也是可以进行读取的。一致性锁定读必须在一个事务中，事务提交或回滚后锁会自动释放。**

### 锁的算法
InnoDB 存储引擎提供了 3 种行锁的算法：
- Record Lock：单个行记录上的锁
- Gap Lock：间隙锁，锁定一个范围但不包含记录自己
- Next-Key Lock：锁定一个范围并包含记录自己

Record Lock 总是会去锁住索引记录，如果 InnoDB 存储引擎表在建立时没有设置任何一个索引，那么这时 InnoDB 存储引擎会使用隐式的主键来进行锁定；当查询的索引含有唯一属性时，InnoDB 存储引擎会对 Next-Key Lock 进行优化，将其降级为 Record Lock，即仅锁住索引本身而不是范围：
><p>示例 1：</p>
>session 1 中对 a=5 的行进行 X 锁定，由于 a 是主键且唯一，因此锁定由 Next-Key Lock 降级为 Record Lock，锁定的仅仅是 a=5 这一行，session 2 的插入不会阻塞，提高了应用的并发性。
```sql
drop table if exists t;
create table t(a int primary key);
insert into t select 1;
insert into t select 2;
insert into t select 5;

session 1:
begin;
select * from t where a = 5 for update;

session 2:
begin;
insert into t select 4;
commit;
```
><p>示例 2：</p>
>session 1 中通过索引 b 查询，因此使用传统的 Next-Key Lock 进行锁定，由于由两个索引，所以需要分别锁定；<br>
>对于聚簇索引 a，Next-Key Lock 降级为 Record Lock，所以只会锁定 a = 5 的行，对于辅助索引 b，Next-Key Lock 锁定的范围是 (1,3)，InnoDB 存储引擎还会对辅助索引下一个键值加上 Gap Lock，即还会有一个范围为 [3,6) 的锁定。</br>
>session 2 的第一条语句会阻塞，因为 a=5 的行上由 X 锁，不能再加 S 锁；第二条语句也会阻塞，因为辅助索引值 2 在锁定范围 (1,3) 内，不能插入；第三条语句也会阻塞，因为辅助索引值 5 在锁定范围 (3, 6) 内；后面三条语句可以执行，因为在锁定范围之外；
```sql
drop table if exists z;
create table z(a int, b int, primary key(a), key(b));
insert into z select 1,1;
insert into z select 3,1;
insert into z select 5,3;
insert into z select 7,6;
insert into z select 10,8;

session 1:
begin;
select * from z where b = 3 for update;

session 2:
begin;
select * from z where a = 5 lock in share mode;
insert into z select 4,2;
insert into z select 6,5;

insert into z select 8,6;
insert into z select 2,0;
insert into z select 6,7;
commit;
```
#### 幻读
幻读是指在同一事务下，连续执行两次相同的 SQL 语句可能导致不同的结果，第二次 SQL 语句可能会返回之前不存在的行。

InnoDB 存储引擎采用 Next-Key Locking 的算法避免幻读问题，对于 ```select * from t where a>2 for update``` 这条语句锁定的不只是 5 这单个值，而是对 (2,) 这个范围加入了 X 锁，因此对于这个范围的插入都是不允许的，从而避免了幻读。
### 锁问题
#### 脏读
脏数据是指事务对缓冲池中行记录的更改并且还没有提交(commit)，如果读到了脏数据即一个事务可以读到另外一个事务中未提交的数据称为脏读。

脏读只有在事务的隔离级别为 Read Uncommitted 时才会发生，InnoDB 存储引擎默认的事务隔离级别是 Read Repeatable，所以默认情况下不会出现脏读。
#### 不可重复读
不可重复读是指在一个事务内多次读取同一数据集间，由于其他事务对该数据集合执行了 DML 操作导致两次读取的数据不一致。

默认情况下 InnoDB 存储引擎中的事务隔离级别是 Read Repeatable，采用 Next-Key Lock 算法来避免不可重复读的问题。
### 阻塞
因为不同锁之间的兼容性关系，在有些时刻一个事务中的锁需要等待另一个事务中的锁释放锁定的资源，这时就发生了阻塞。

在 InnoDB 存储引擎中，参数 ```innodb_lock_wait_timeout``` 用来控制等待的时间(默认 50s)，可以动态设置；```innodb_rollback_on_timeout``` 用来设定是否超时回滚(默认 off，表示不回滚)，只能在启动前配置：
```sql
set @innodb_lock_wait_timeout=60;
```
默认情况下 InnoDB 存储引擎不会回滚超时引发的错误异常，因此在发生超时导致的异常时需要判断是需要 commit 还是 rollback。
### 死锁
死锁是指两个或两个以上的事务在执行过程中因争夺锁资源而造成的一种互相等待的现象。

InnoDB 存储引擎采用等待图(wait-for graph)检测死锁，等待图要求数据库保存两种信息：
- 锁的信息链表
- 事务等待链表

通过这两种信息可以构造出一张图，如果这个图中存在回路则代表存在死锁，因此资源间相互等待。

时间|会话1|会话2
-|-|-
1|BEGIN;|
2|SELECT * FROM t WHERE a=1 FOR UPDATE;|BEGIN;
3||SELECT * FROM t WHERE a=2 FOR UPDATE;
4|SELECT * FROM t WHERE a=2 FOR UPDATE|
5||SELECT * FROM t WHERE a=1 FOR UPDATE

### 锁升级
锁升级是指将当前锁的粒度降低。InnoDB 存储引擎不存在锁升级的问题，因为其根据每个事务访问的每个页对锁进行管理的，采用的是位图的方式。因此不管一个事务锁住页中的一个记录还是多个记录，开销通常都是一致的。

### 行锁优化
InnoDB 存储引擎的行级锁定是通过索引实现的，存在一些性能上的隐患：
- 当 query 无法利用索引的时候，InnoDB 会放弃使用行级锁定而改用表级别锁定，造成并发性能降低
- 当 query 使用的索引并不包含所有过滤条件时，数据检索使用到的索引键中的数据可能有部分并不属于该 query 的结果集但是也会被锁定
- 当 query 在使用索引定位数据的时候，如果使用的索引键一样但访问的数据行不同也会被锁定

由于索引实现的行级锁存在的隐患，所以需要合理利用 InnoDB 的行级锁定：
- 尽可能让所有的数据检索都通过索引来完成，避免行级锁升级为表级锁
- 合理设置索引，尽可能的缩小锁的范围(提高索引的 cardination)
- 尽可能减少基于范围的数据检索而减少间隙锁的影响
- 尽可能控制事务的大小，减少锁定资源的时间

**[Back](../../)**