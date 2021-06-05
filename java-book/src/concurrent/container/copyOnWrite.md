## CopyOnWrite

CopyOnWrite 指的是数据在写的时候并不是在原数据上写，而是将原数据拷贝一份进行修改，然后再通过悲观锁或者乐观锁的方式写回。

### CopyOnWriteArrayList

CopyOnWriteArrayList 底层的数据结构是一个数组，在读的时候并不需要加锁，但是在写入的时候为了保证并发安全会先拷贝一份源数据，然后再写入。
```java
public boolean add(E e) {
    // 写之前加悲观锁
    final ReentrantLock lock = this.lock;
    lock.lock();
    try {
        Object[] elements = getArray();
        int len = elements.length;
        // 拷贝数据
        Object[] newElements = Arrays.copyOf(elements, len + 1);
        newElements[len] = e;
        // 将数组变量引用到新的数组地址上
        setArray(newElements);
        return true;
    } finally {
        lock.unlock();
    }
}
```
###  CopyOnWriteArraySet
CopyOnWriteArraySet 的底层是使用数组实现的一个 Set，保证所有的元素不重复，其底层实现采用 CopyOnWriteArrayList。
```java
// 初始化 CopyOnWriteArraySet
public CopyOnWriteArraySet() {
    al = new CopyOnWriteArrayList<E>();
}

// 添加元素
public boolean add(E e) {
    return al.addIfAbsent(e);
}
```
