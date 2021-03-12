## 字符串

Redis 字符串命令用于管理 Redis 中的字符串值，字符串对象的  type 值为 OBJ_SRING，encoding 则根据字符串对象保存的值的类型有不同的值：

- 如果字符串对象保存的是整数并且这个整数值可以用 long 类型来表示，则 encoding 为 `OBJ_ENCODING_INT` 且 ptr 保存了整数值
- 如果字符串对象保存的是字符串值并且这个字符串值的长度大于 32 字节，那么字符串对象使用简单动态字符串(SDS)保存字符串值并将 encoding 设置为 `OBJ_ENCODING_RWA`
- 如果字符串对象保存的是字符串值并且这个字符串值的长度小于等于 32 字节，那么字符串对象将使用 `OBJ_ENCODING_INT` 编码方式保存字符串值

### 设置字符串

Redis 通过设置字符串命令将字符串设置到数据库，通过不同的命令行参数可以指定字符串的超时时间、设置条件等。

#### `SET`

`set` 命令将 key-value 设置到数据库，如果key已经设置，则set会用新值覆盖旧值，不管原value是何种类型，如果在设置时不指定EX或PX参数，set命令会清除原有超时时间。

`set` 命令的时间复杂度为 O(1)。

```
SET key value [EX second | PX milliseconds | EXAT timestamp | PXAT millisecond-timestamp | KEEPTTL] [NX|XX] [GET]
```

`set` 命令有多个可选的参数，这些参数限制了命令的行为：

- `EX | PX | EXAT | PXAT`：设置过期时间或者在指定的时间过期
- `NX | XX`：只有在 key 不存在/存在 时才能设置成功
- `KEEPTTL`：保留 key 上关联的过期时间
- `GET`：返回 key 中存储的旧值，如果没有则返回 nil

#### `MSET`

`mset` 命令可以设置多个 key-value 到数据库，如果已经存在则会覆盖。`mset` 操作是原子的，不会出现某些 key-value 设置了而某些 key-value 没有设置这种中间状态。

`mset` 的时间复杂度为 O(N)。

```
MSET key value [key value ...]

MSETNX key value [key value ...]
```

`msetnx` 在设置 key-value 时会检查是否有 key 存在，如果有则所有的设置都不会生效。

### 修改字符串

修改字符串操作可以直接在原有字符串上修改，而不需要先获取字符串修改后再覆盖原有字符串。

#### `APPEND`

`append` 命令可以直接在原有的字符串后追加，如果 Key 不存在则创建。

`append` 命令的时间复杂度为 O(1)，因为字符串 sds 扩容时会预留空间。

```
APPEND key value
```

`append` 命令可以用于存储固定长度的紧凑型时间序列。

#### `SETRANGE`

`setrange` 命令用于覆盖字符串从指定位置开始的字串，如果指定位置超过字符串长度则使用空字节填充。

`setrange` 的时间复杂度在不计算扩容时为 O(1)，在触发扩容时时间复杂度为 O(M)。

```
SETRANGE key offset value
```

### 计数器命令 

计数器命令包括 `INCR/DECR`、`INCRBY/DECRBY`、`INCRBYFLOAT` 这几个命令，Redis 提供的计数命令是原子的，通常用于统计计数。

```
incr mykey
```

