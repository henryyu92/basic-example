### 参数文件
参数文件用于在 MySQL 实例启动时指定数据库文件的位置并指定初始化参数。MySQL 实例在启动时会按照顺序在指定的位置进行读取，通过命令 ```mysql --help | grep my.cnf``` 查看：
```shell
mysql --help | grep my.cnf

/etc/my.cnf /etc/mysql/my.cnf ~/.my.cnf
```
参数文件中也可以不指定参数，因为 MySQL 实例启动时会有参数的默认值，使用命令 ``` show variables``` 可以查看数据库中所有的参数。
### 日志文件
日志文件记录影响 MySQL 数据库的各种活动，MySQL 数据库中常见的日志文件有：
- 错误日志(error log)
- 二进制日志(binlog)
- 慢查询日志(slow query log)
- 查询日志(log)
#### 错误日志
错误日志对 MySQL 的启动、运行和关闭过程进行了记录，该日志不仅记录了所有的错误信息，也记录了一些警告信息和正确地信息。

可以通过命令 ```show variables like "log_error"``` 来定位该文件的位置，该命令可以查看错误文件的文件名：
```sql
show varaibles like 'log_error';

+---------------+--------|
| Variable_name | Value  |
+---------------+--------+
| log_error     | stderr |
+---------------+--------+
```
#### 慢查询日志
慢查询日志定位可能存在问题的 SQL 语句，从而进行 SQL 语句层面的优化。可以在 MySQL 启动时设置一个阈值，运行超过该阈值的所有 SQL 语句都记录到慢查询日志中，该阈值可以通过参数 ```long_query_time``` 来设置，默认为10秒：
```sql
show variables like "long_query_time";

+-----------------+-----------|
| Variable_name   | Value     |
+-----------------+-----------+
| long_query_time | 10.000000 |
+-----------------+-----------+
```
默认情况下，MySQL 并不会开启慢查询日志，需要手动开启：
```sql
show variables like 'slow_query_log';
```
参数 slow_query_log_file 指定慢查询日志问文件存放的位置：
```sql
show variables like 'slow_query_log_file';
```
如果运行的 SQL 没有使用索引，则 MySQL 同样会将这条 SQL 语句记录到慢查询日志文件，使用 log_queries_not_using_indexes 参数设定是否开启：
```sql
show variables like 'log_queries_not_using_indexes';
```
参数 log_throttle_queries_not_using_indexes 表示每分钟允许记录到 slow log 的且没有使用索引的 SQL 的语句次数，默认是 0 表示没有限制：
```sql
show variables like 'log_throttle_queries_not_using_indexes';
```
通过满查询日志可以找出有问题的 SQL 语句进行优化，使用 MySQL 提供的 mysqldumpslow 命令可以直观的分析慢查询日志：
```sql
mysqldumpslow slow_log_name.log
```
慢查询日志可以记录到 ```mysql.slow_log``` 表中从而可以直观的查询，使用 log_output 参数指定慢查询日志的输出格式为 TABLE(默认是 FILE) 就可以使慢查询日志记录到 mysql.slow_log 中：
```sql
show variables lilke 'log_output';

set globle log_output='TABLE';
```
#### 查询日志
查询日志记录了所有对 MySQL 数据库请求的信息，无论这些请求是否得到了正确的执行。默认文件名是 ```host_name.log```

查询日志记录也可以放入 mysql.general_log 表中，将 log_output 参数设置为 TABLE 即可。
#### 二进制日志
二进制日志(binary log)记录了对 MySQL 数据库执行更改的所有操作，但是不包括 select 和 show 这类操作，因为这类操作对数据本身并没有修改。即使更新操作没有导致数据库发生变化也会写入二进制文件。

二进制日志主要有几个作用：
+ 恢复(recovery)：某些数据的恢复需要二进制文件
+ 复制(replication)：通过复制和执行二进制日志使一台远程的 MySQL 数据库(一般称为 slave)与一台 MySQL 数据库(一般称为 master) 进行实时同步
+ 审计(audit)：用户可以通过二进制日志文件中的信息来进行审计，判断是否有对数据库进行注入攻击  

通过配置 log-bin 可以启动二进制日志，二进制日志的路劲为数据库所在目录(datadir)：
```sql
show variables like 'log_bin';

show varaibles like 'datadir';
```
影响二进制日志的信息和行为的配置参数：
- ```max_binlog_size```：指定单个二进制日志文件的最大值，如果超过该值，则产生新的二进制日志文件，后缀+1并记录到 index 文件中
- ```binlog_cache_size```：当使用事务的表存储引擎时，所有未提交(uncommitted)的二进制日志会被记录到一个缓存中去，等该事务提交(commit)时直接将缓冲中的二进制日志写入二进制日志文件中，缓冲大小由 binlog_cache_size 决定，默认 32K：
  ```sql
  show varaibles like 'binlog_cache_size';
  ```
  binlog_cache_size 是基于会话(session)的，也就是会所当一个线程开始一个事务时，MySQL 会自动分配一个大小为 binlog_cache_size 的缓存，因此 binlog_cache_size 设置需要合适，可以使用 ```show global status``` 命令查看 binlog_cache_use、binlog_cache_disk_use 的状态来判断 binlog_cache_size 的设置是否合理；其中 binlog_cache_use 记录了使用缓冲写二进制日志的次数，binlog_cache_disk_use 记录了使用临时文件写二进制日志的次数：
  ```sql
  show global status like 'binlog_cache_use';
  show global status like 'binlog_cache_disk_use';
  ```
- ```sync_binlog```：默认情况下，二进制日志并不是在每次写的时候同步到磁盘，因此在数据库发生宕机时可能会有一部分数据没有写入二进制日志文件中。参数 sync_binlog=[N] 设置每写多少次缓冲就同步到磁盘，默认是 1 表示不使用操作系统的缓冲来写二进制日志：
  ```sql
  show variables like 'sync_binlog';
  ```
- ```binlog-do-db```：表示需要写入哪些库的日志，默认为空，表示需要同步所有库的日志到二进制日志
- ```binlog-ignore-db```：表示需要忽略哪些库的日志
- ```log-slave-update```：如果当前数据库是 slave 角色则不会将从 master 获取并执行的二进制日志写入自己的二进制日志中，如果需要写入则需要设置
- ```binlog_format```：该参数可设置的值有 STATEMENT、ROW 和 MIXED
  - STATEMENT：二进制日志文件记录的是日志的逻辑 SQL 语句
  - ROW：记录表的行更改情况
  - MIXED：默认使用 STATEMENT 格式记录二进制日志文件，一些情况下使用 ROW 格式
  
  可以设置 binlog_format 来更改二进制日志写入的格式，默认为 ROW：
  ```sql
  show variables like 'binlog_format';
  ```
查看二进制文件的内容可以使用 ```mysqlbinlog``` 工具，如果二进制文件是 STATEMENT 格式则可以直接看到 SQL 语句，如果是 ROW 格式则通过 -vv 参数就能看到具体的执行信息了：
```sql
mysqlbinlog --start_position=12345 test_log.0001
```
### 表结构文件
MySQL 数据的存储是根据表进行的，每个表都会有与之对应的文件，无论表采用何种存储引擎，MySQL 都有一个以 frm 为后缀的文件记录该表的表结构定义：
```shell
cat test_table.frm
```
### InnoDB 存储引擎文件
#### 表空间文件
InnoDB 将存储的数据按表空间(tablespace)进行存放，默认配置下会有一个初始化大小为 10M 名为 iddata1 的文件是默认的表空间文件，可以通过参数 ```innodb_data_file_path``` 对其进行设置：
```sql
set innodb_data_file_path='file_path'
```
设置了 innodb_data_file_path 之后所有基于 InnoDB 存储引擎的表的数据都会记录到该共享表空间中。如果设置了 ```innodb_file_per_table``` 参数则会将每个基于 InnoDB 存储引擎的表产生一个后缀名为 .idb 的独立表空间：
```sql
show variables like 'innodb_file_per_table';
```
#### 重做日志
默认情况下，在 InnoDB 存储引擎的数据目录下会有两个名为 ib_logfile0 和 ib_logfile1 的文件，称为重做日志(redo log)文件，它们记录了对于 InnoDB 存储引擎的事务日志。

重做日志文件有几个比较重要的参数：
- ```innodb_log_file_size```：指定每个重做日志的大小
- ```innodb_log_files_in_group```：指定日志文件组中重做日志文件的数量，默认是 2
- ```innodb_mirrored_log_groups```：指定了日志镜像文件组的数量，默认是 1
- ```innodb_log_group_home_dir```：指定了日志文件组所在的路劲，默认为 ./ 表示在 MySQL 数据库的数据目录下
