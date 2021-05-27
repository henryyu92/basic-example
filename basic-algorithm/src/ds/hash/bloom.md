## 布隆过滤器

布隆过滤器用于检索一个元素是否在一个集合中
- 优点是空间复杂度和时间复杂度都非常优良
- 缺点是存在一定的误识别率和删除困难

布隆过滤器是一个长度为 m 的 bit 型数组


使用 int 类型标识 bit 型数组，查找某个数所在的 bit 位时：
```java
intIndex = index /32;
bitIndex = index % 32;

arr[intIndex] = arr[intIndex] | (1 << bitIndex);
```