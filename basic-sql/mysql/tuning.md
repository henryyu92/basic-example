## 调优

MySQL 调优一般是通过慢查询日志找到需要优化的 SQL，然后通过 explain 信息得到该 SQL 的执行计划，根据执行计划调整 SQL。

### Explain
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

### 索引优化

- 如果列经常出现在 where 查询条件中，并且列的分布均匀，则建立索引效果比较好
- 回表的次数和辅助索引查询出的数量相同，因此如果使用辅助索引最好减少回表，通常可以使用联合索引来直接获取需要的列，也就是说在查询中最好不要使用 `select * ` 这种查询

### 表关联

在多表连接的时候，一般情况下是两个表先关联，关联之后再和其他表关联。

#### 嵌套循环

驱动表返回每行数据都传值给被驱动表用于查询，因此驱动表返回多少行，被驱动表就需要执行多少次扫描

嵌套循环驱动表需要返回较少的数据，而被驱动表则必须使用索引。

对于外连接来说驱动表固定了，此时需要保证被驱动表能够走索引

```sql
-- b.id 建立了索引
select a.*, b.* from a join b on a.id = b.id
```



#### 哈希连接

哈希连接是两个表的等值连接，将较小的表作为驱动表，将驱动表的 select 列和 join 列读取到内存中并对驱动表的连接列进行 hash 运算，然后被驱动表的连接列进行 hash 运算并和驱动表进行比较。如果驱动表比较大则会产生磁盘 hash 连接，此时性能就会严重下降。

```sql
-- b.id 没有设置索引
select a.*, b.* a join b on a.id = b.id
```

嵌套循环每循环一次会将驱动表连接列的值传给被驱动表，而哈希连接没有传值的过程，而是直接比较，因此 hash 连接不需要索引。

对于哈希连接需要避免使用 `select * from` 这种写法，减少内存的占用。

#### 排序合并连接

排序合并连接主要用于处理两表非等值关联，比如 `>`、`>=`、`<`、`<=`、`<>` 等。

```sql
select * from a, b where a.id >= b.id
```



### 表结构优化

#### 主键

在 Innodb 存储引擎中，如果定义了主键则会将主键作为聚簇索引，如果没有显式定义主键则会选择第一个不包含 null 值的唯一索引作为主键索引。

主键一般设计为自增的，使用自增主键时每次插入新的记录就会顺序添加到当前索引节点的后续位置，当一页写满就会自动开辟一个新的页；如果使用非自增索引(uuid)，由于每次插入主键的值近似与随机，因此每次插入新纪录都需要被插入到索引页的随机位置，造成索引碎片。

自增主键不应该带有业务含义，如果业务数据变动导致不是自增的，后续插入的数据主键比前面的小，可能引发页分裂，产生空间碎片。

### 分库分表

水平分表：以**字段**为依据，按照一定策略（hash、range等），将一个**表**中的数据拆分到多个**表**中

- 每个表的结构都一样
- 每个表的数据都不一样，没有交集
- 所有表的幷集是全量数据

垂直分表：以**表**为依据，按照业务归属不同，将不同的字段拆分到不同的表中

- 每个表的数据结构不同
- 表中的数据也不同，并且没有交集
- 所有表的幷集是全量数据



**[Back](../../)**