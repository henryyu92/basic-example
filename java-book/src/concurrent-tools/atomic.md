## Atomic
Java 提供了 java.util.concurrent.atomic 包，这个包中的原子操作类提供了线程安全的更新，Atomic 类基本都是使用 Unsafe 实现的包装类。

### Unsafe

`Unsafe` 是整个 Concurrent 包的基础，里面所有的方法都是 native 的，通过 cas 的方式无锁的修改贡献变量。`Unsafe` 提供了三个 cas 方式修改数据的方法：

```java
// 采用 cas 方式更新对象的引用
public final native boolean compareAndSwapObject(Object o, long offset,
                                                 Object expected,
                                                 Object x);
// 采用 cas 方式更新 int 数据
public final native boolean compareAndSwapInt(Object o, long offset,
                                              int expected,
                                              int x);
// 采用 cas 方法更新 long 数据
public final native boolean compareAndSwapLong(Object o, long offset,
                                               long expected,
                                               long x);
```

`Unsafe` 提供的 cas 修改数据方法需要提供 4 个参数：

1. 需要修改的目标对象
2. 需要修改的目标字段
3. 目标字段的新值
4. 目标字段的旧值



### 更新基本类型
- AtomicBoolean：原子更新布尔类型
- AtomicInteger：原子更新整型
- AtomicLong：原子更新长整型
```java
public class AtomicIntegerTest {

    public static AtomicInteger ai = new AtomicInteger(1);
    public static void main(String[] args) {
        System.out.println(ai.getAndIncrement());
        System.out.println(ai.get());
    }
}
```
### 更新数组
- AtomicIntegerArray：原子更新整型数组里的元素
- AtomicLongArray：原子更新长整型数组里的元素
- AtomicReferenceArray：原子更新引用类型数组里的元素
```java
public class AtomicIntegerArrayTest {

    // AtomicIntegerArray 是将数组进行了复制然后再对数组修改，并不会影响原数组
    public static int[] value = new int[]{1, 2};
    public static AtomicIntegerArray ai = new AtomicIntegerArray(value);
    public static void main(String[] args) {
        ai.getAndSet(0, 3);
        System.out.println(ai.get(0));
        System.out.println(value[0]);
    }
}
```
### 更新引用
- AtomicReference：原子更新引用类型
- AtomicReferenceFieldUpdater：原子更新引用类型里的字段
- AtomicMarkableReference：原子更新带有标记位的引用类型
```java
public class AtomicReferenceTest {
    public static AtomicReference<User> ar = new AtomicReference<>();
    public static void main(String[] args) {
        User user = new User("conna", 10);
        ar.set(user);
        User upUser = new User("Shinichi", 17);
        ar.compareAndSet(user, upUser);
        System.out.println(ar.get().getName());
    }

    static class User{
        private String name;
        private int old;
        public User(String name, int old){
            this.name = name;
            this.old = old;
        }
        public String getName(){
            return this.name;
        }
    }
}
```
### 更新属性
- AtomicIntegerFieldUpdater：原子更新整型的字段的更新器
- AtomicLongFieldUpdater：原子更新长整型的更新器
- AtomicStampedReference：原子更新带有版本号的引用类型，可以用于解决使用 CAS 进行原子更新时出现的 ABA 问题

要想原子的更新字段类需要两步：使用静态方法 ```newUpdater()``` 创建一个更新器并设置想要更新的类和属性；更新类的使用 public volatile 修饰的字段。
```java
public class AtomicIntegerFieldUpdaterTest{
    private static AtomicIntegerFieldUpdater<User> a = AtomicIntegerFieldUpdater.newUpdater(User.class, "old");
    public static void main(String[] args){
        User conan = new User("conan", 10);
        System.out.println(a.getAndIncrement(conan));
        System.out.println(a.get(conan))
    }

    public static class User{
        private String name;
        public volatile int old;
        public User(String name, int old){
            this.name = name;
            this.old = old;
        }
    }
}
```