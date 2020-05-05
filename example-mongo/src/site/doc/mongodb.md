## MongoDB 服务器启动
```shell
mongod --dbpath /mongo/data/path	存储文件目录
       --bind_ip					绑定访问 IP，默认所有 IP
	   --logpath					指定日志文件
	   --logappend					使用追加方式写日志
	   --dbpath						指定数据库路径
	   --port						指定暴露服务的端口，默认 27017
	   --serviceName				指定服务名称
	   --serviceDisplayName			指定服务名称，多个服务时执行
```
## MongoDB 客户端连接
```shell
mongo server_ip:port/dbname -u user -p password
```
## MongoDB 概念
- database：数据库，和 rdbms 中的数据库概念一致
- collection：集合，类似 rdbms 中数据表的概念
- document：文档，类似 rdbms 中行的概念
- field：字段，类似 rdbms 中列的概念
- index：索引，和 rdbms 中的概念一致
- primary key：主键，和 rdbms 中主键概念一致，但是 MongoDB 会自动将 _id 设置为主键
## MongoDB curd 操作
### create 操作
>*create 或者 insert 操作将 document 新增到 collection，如果 collection 不存在则会自动创建 collection*
#### 单个 document 插入
```sql
db.collection_name.insertOne(
	<document>,
	{
		writeConcer: <document>
	}
)

db.inventory.insertOne(
	{item:"canvas", qty:100, tags:["cotton"], size:{h:28, w:35.5, uom:"cm"}}
)
插入一条 document 到 inventory 这个 collection，如果 inventory 不存在则创建
```
#### 批量插入 document
```sql
db.collection_name.insertMany(
	[<document_1>, ...],
	{
		"writeConcer": <document>,
		ordered: <boolean>
	}
)

db.inventory.insertMany([
	{item:"journal", qty:25, tags:["blank", "red"], size:{h:14, w:22.85, uom:"cm"}},
	{item:"mat", qty:85, tags:["gray"]}
])
```
### update 操作
> MongoDB 提供了多种更新操作符用于更新 document
- db.collection.updateOne(<filter>, <update>, <options>)：更新首个满足条件的 document
```sql
db.inventory.updateOne(
	{item:"paper"},
	{
		$set:{"size.uom":"cm", status:"p"},
		$currentDate: {lastModified: true}
	}
)

更新第一个 item = paper 的 document
$set 操作符将 size.uom 字段更改为 cm，status 更改为 p
$currentDate 操作符将 lastModified 更改为当前时间，如果 lastModified 不存在则创建

```
- db.collection.updateMany(<filter>, <update>, <options>)：更新所有满足条件的 document
```sql

```
- db.collection.replaceOne(<filter>, <update>, <options>)
### delete 操作
### bulk write 操作
## 数据模型
## 事物
## 索引
## 副本
## 分片