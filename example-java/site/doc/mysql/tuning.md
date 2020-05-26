### 调优
MySQL 调优一般是通过慢查询日志找到需要优化的 SQL，然后通过 explain 信息得到该 SQL 的执行计划，根据执行计划调整 SQL。

SQL 优化主要包含索引优化、表结构优化以及业务优化三个方面：

#### explain 信息：
  + id：MySQL Query Optimizer 选定的执行计划中查询的序列号
  + select_type: 所使用的查询类型，主要有以下几种查询类型
    - DEPENDENT SUBQUERY: 子查询内层的第一个 select，依赖喻外部查询的结果集
    - DEPENDENT UNION：子查询中的 union 且为 union 中从第二个 select 开始的后面所有 select，依赖于外部查询的结果集
    - PRIMARY：子查询中的最外层查询，注意并不是主键查询
    - SIMPLE：除子查询或 union 之外的其他查询；
    - SUBQUERY：子查询内层查询的第一个 select 结果不依赖于外部查询
    - UNCACHEABLE SUBQUERY：结果集无法缓存的子查询
    - UNION：union 语句中的第二个 select 开始后面的所有 select，第一个 select 为 PRIMAY
    - UNION RESULT：union 中的合并结果
  + table：显示这一步所访问的数据库中的表的名称
  + type：表的访问方式，主要包含如下类型
    - all：全表扫描
    - const：读常量，最多只会有一条记录匹配，由于是常量，实际上只需要读一次
    - eq_ref：最多只会有一条匹配结果，一般是通过主键或唯一索引来访问
    - fulltext：进行全文索引检索
    - index：全索引扫描
    - index_merge：查询中同时使用两个(或更多)索引，让后对索引结果进行合并(merge)，再读取表数据
    - index_subquery：子查询中的返回结果字段组合是一个索引(或索引组合)，但不是一个主键或唯一索引
    - range：索引范围扫描
    - ref：join 语句中被驱动表索引引用的查询
    - ref_or_null：与 ref 的区别就是在使用索引引用的查询之外再增加一个空值的查询
    - system：系统表，表中只有一行数据
    - unique_subquery：子查询中的返回结果字段组合是主键或唯一约束
  + possible_keys：该查询可以例用的索引，如果没有任何索引可以使用，就会显示成 null
  + key：MySQL Query Optimizer 从 possible_keys 中所选择使用的索引
  + key_len：被选中使用索引的索引长度
  + ref：列出是通过常量(const)，还是某个表的某个字段(如果是join)来过滤(通过 key) 的
  + rows：Mysql Query Optimizer 通过系统统计收集的统计信息估算出来的结果集记录条数
  + extra：查询中每一步实现的额外细节信息
    - distinct：查找 distinct 值，当 musql 找到了第一条匹配的结果时，将停止该值的查询，转为后面的其他值查询
    - full scan on null keys：子查询中的一种优化方式，主要在遇到无法通过索引访问 null 值的时候使用
    - impossible where noticed after reading const table：MySQL Query Optimizer 通过收集到的统计信息判断出不可能存在的结果
    - no tables：query 语句中使用 from dual 或者不包含 from 字句
    - not exist：在某些连接中，mysql query optimizer 通过改变原有的 query 组成而使用的优化方法，可以部分减少数据访问次数
    - range checked for each record 
    - using filesort：包含order by 但是无法通过索引完成排序，只能通过排序算法实现
    - using index：所需数据只需要在 index 即可获取全部数据，不需要到表中取
    - using index for group-by 分组字段也在索引中
    - using temporary：使用临时表，常见于 group by 和 order by 操作
    - using where：不读取表的所有数据，或不是通过索引可以获取所有需要的数据，则会出现 using where

#### 索引优化
- B-Tree 索引
除了 Archive 存储引擎外的所有存储引擎均支持 B-Tree 索引；  
B-Tree 索引是以 Balance Tree 的数据结构存储数据，也就是所有的数据存放于 Tree 的 Leaf Node，并且到任何一个 Leaf Node 的最短路劲都是相同的；InnoDB 存储引擎使用的是 B+Tree，在每一个 Leaf Node 上额外存储了指向下一个相邻的 Leaf Node 的指针
- Hash 索引
- Fulltext 索引
- R-Tree 索引

#### 表结构优化

1. Mysql 自增主键
如果定义了主键，Innodb 会选择主键作为聚簇索引，如果没有显式定义主键则 innodb 会选择第一个不包含 null 值的唯一索引作为主键索引。

使用自增主键那么每次插入新的记录就会顺序添加到当前索引节点的后续位置，当一页写满就会自动开辟一个新的页；如果使用非自增索引(uuid)，由于每次插入主键的值近似与随机，因此每次插入新纪录都需要被插入到索引页的随机位置，造成索引碎片。

自增主键不应该带有业务含义，如果业务数据变动导致不是自增的，后续插入的数据主键比前面的小，可能引发页分裂，产生空间碎片。

2. Mysql 自建定义为 NOT NULL
如果字段可以包含 null 需要更多的存储空间，可为 null 的列被索引后，每条记录都需要一个额外的字节


**[Back](../../)**