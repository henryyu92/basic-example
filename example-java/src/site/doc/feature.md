### Lambda 表达式
Lambda 表达式的基本结构：
```
(type param1,type param2,...) -> {function_body}
```
- 参数列表和接口中方法的参数列表对应
- 如果参数类型可以推导则 type 可以省略
- 如果只有一个参数且类型可推导则可以省略 ```()```
- 如果函数体只有一条语句则可以省略 ```{}```

在 Java 中 Lambda 表达式是对象，必须依赖于函数式接口。
```java
public interface TestInterface{
    public void test();
}

// 直接使用 Lambda 创建对象，必须指定返回的类型用于推断 Lambda 表达式
TestInterface f = () ->{}
```
### 函数式接口

函数式接口是只有一个抽象方法的接口。

- 如果接口上声明了 ```@FunctionalInterface``` 注解，那么编译器会按照函数式接口的定义来要求该接口
- 如果接口只有一个抽象方法但并没有被 ```@FunctionalInterface``` 注解，那么编译器依旧会将该接口看做函数式接口
- 如果接口的抽象方法重写了 java.lang.Object 的 public 方法则不会被认为是函数式接口
- 函数式接口的实例可以通过 Lambda 表达式、方法引用和构造器引用创建

```java
@FunctionalInterface
public interface TestInterface{
    void test();
    // 默认方法不会影响抽象方法的数量，即满足函数式接口的定义
    default void testDefault(){}
    // 重写 Object 的 public 方法不会影响抽象方法的数量，即满足函数式接口的定义
    String toString();
}
```
#### Consumer
接口的抽象方法 ```accept``` 接收一个参数并且不返回结果
```java
Consumer<String> c = s -> {
    String hello = "hello, " + s;
    System.out.println(hello);
};
// "hello, world"
c.accept("world");
```
默认方法 ```andThen``` 接收一个 Consumer 类型的参数 (after) 并返回 Comsumer 类型。返回新的 Comsumer 类型的 Lambda 表达式对象重写的 accept 方法中先调用了当前对象 (c) 的 accept 方法，然后再调用 Consumer 类型参数对象 (after) 的 accept 方法：
```java
// "hello, bad boy"
// "get out, bad boy"
c.andThen(s -> System.out.println("get out, " + s)).accept("bad boy");
```
#### Function
抽象方法 apply 接收一个参数并返回一个结果
```java
Function<Integer, Integer> f = value -> 2 * value;
// "2"
System.out.println(f.apply(1));

```
默认方法 compose 接收一个 Function 类型的参数 (befor) 并返回 Function 类型。返回的 Function 类型对象的 apply 方法先执行了参数对象 (before) 的 apply 方法，然后将其结果作为参数执行了当前对象 (f) 的 apply 方法：
```java
// "2"
Integer v = f.compose(value -> value * value).apply(1);

```
默认方法 andThen 和 compose 相反，返回的 Function 类型对象的 apply 方法先执行当前对象(f)的 apply 方法，然后将其结果作为参数执行输入参数对象 (after) 的 apply 方法：
```java
// "4"
Integer v = f.andThen((Integer value) -> value * 3).apply(1);
```
静态方法 identity 不接收参数，返回一个 Function 类型对象。返回的 Function 对象的 apply 方法返回输入的参数：
```java
// "1"
Function.identity().apply(1);
```
#### BiFunction
抽象方法 apply 接收两个参数并返回一个结果
```java
BiFunction<Integer, Integer, Integer> bf = (a, b) -> a + b;
// "3"
bf.apply(1, 2);
```
默认方法 andThen 接收一个 Function 类型参数并返回 BiFunction 类型对象。返回的 BiFunction 类型对象的 apply 方法在调用时会先调用当前对象 (bf) 的 apply 方法，然后再将其结果作为参数调用 Function 类型参数对象 (after) 的 apply 方法：
```java
// "9"
bf.andThen(value -> value * value).apply(1, 2);
```
#### Predicate
抽象方法 test 接收一个参数并返回一个 boolean 类型的结果
```java
Predicate<Integer> p = v -> v.length() >= 5;
// "true"
p.test("hello")  
```
默认方法 and 传入一个 Predicate 类型参数，当两个 Predicate 应用 test 方法都为 true 时返回 true；or 当两个 Predicate 的 test 有一个为 true 则返回 true；negate 方法为取反：
```java
public void conditionFilter(List<Integer> list, Predicate<Integer> p1, Predicate<Integer> p2){
    list.forEach(a -> p1.and(p2),negate().test(a) ? System.out.Println(a))
}

conditionFilter(Arrays.asList(1,2,3,4,5,6,7,8,9,10), item->item>5, item->item%2==0);
```
静态方法 isEqual 判断两个对象是否相等，内部是使用 equals 方法判断：
```java
Predicate.isEqual("test").test("test");
```
#### Supplier
抽象方法 get 不接收参数并返回一个结果
```java
Supplier<String> supplier = ()->"Hello world";
supplier.get();  // Hello world
```

### 方法引用
方法引用实际是 Lambda 表达式的一种语法糖。方法引用分为 4 类：
- 类名::静态方法名
- 对象名::实例方法名
- 类名::实例方法名，需要 Lambda 表达式的第一个参数是调用实例
- 类名::new

```java
public class Student{
    private int score;

    public static int compare(Student s1, Student s2){
        return s1.getScore() - s2.getScore();
    }

    public int compareNew(Student s){
        return this.socre - s.getScore();
    }
}
// 类名::静态方法名
List<Student> students = Arrays.asList(s1, s2, s3, s4);
students.sort(Student::compare)

public class StudentComparator{
    public int compare(Student s1, Student s2){
        return s1.score - s2.score;
    }
}
// 对象名::实例方法名
StudentComparator comparator = new StudentComparator();
students.sort(comparator::compare);

// 类名::实例方法名
students.sort(Student::compareNew);
```
#### 默认方法
接口中可以定义默认方法作为实现类的默认实现，如果一个类实现的多个接口有相同的 default 方法则会抛出错误，此时实现类必须要重写默认方法。
```java
public class TestClass implements TestInterface1, TestInterface2{
    @override
    public void testMethod(){
        TestInterface2.super.testMethod();
    }
}
```
### 流式编程
流由 3 部分组成：
- 数据源
- 零个或多个中间操作
- 终止操作

流的特性：
- 流不存储值，通过管道的方式获取值
- 流不会修改底层的数据源
- 流是惰性的；只有在启动终止操作时才对源数据执行计算，并且仅根据需要使用源元素
```java
// 根据数据创建流
Stream stream = Stream.of("hello", "world");
// 根据集合创建流
stream = Arrays.asList("hello", "world").stream();
// iterator 生成流
Stream.iterator(1, item->item+2).limit(6).forEach(System.out::println);

// 32
Stream.iterator(1, item->item+2).limit(6).filter(item->item>2).mapToInt(t->t*2).skip(2).limit(2).sum();

// 3,4,5,6,7
IntStream.range(3,8).forEach(System.out::println);

// 42
Arrays.asList(1,2,3,4,5,6).stream().map(i->2*i).reduce((0, Integer::sum));


Stream.of("hello", "world").collect(LinkedList::new, LinkedList::add, LinkedList::addAll);
// ArrayList
Stream.of("hello", "world").collect(Collectors.toList())
// LinkedList
Stream.of("hello", "world").collect(Collectors.toCollection(LinkedList::new))
```
#### Optional 对象
Optional 是一个容器对象，其中可能包含也可能不包含一个非空的值，如果包含一个非空值时 isPresent 方法返回 true，get 方法会返回非空值。
```java
Optional<String> optional = Optional.of("hello");
// 获取 Optional 中的元素
optional.ifPresent(item -> System.out.println(item));
optional.orElese("world");
optional.orEleseGet(()->"world");
```
### 日期与时间