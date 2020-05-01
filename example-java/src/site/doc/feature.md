### 默认方法
JDK 8 接口中可以定义默认方法作为实现类的默认实现，默认方法使用 default 关键字修饰。

当类中的方法和默认方法相同时，类中的方法会覆盖接口中的默认方法，如果一个类实现的多个接口有相同的 default 方法则会抛出错误，此时实现类必须要重写默认方法。
```java
public class TestClass implements TestInterface1, TestInterface2{
    @override
    public void testMethod(){
        TestInterface2.super.testMethod();
    }
}
```
### 静态方法
JDK 8 接口中可以定义静态方法，静态方法使用关键字 static 修饰，

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
Predicate<String> p = v -> v.length() >= 5;
// "true"
p.test("hello")  
```
默认方法 and 传入一个 Predicate 类型参数并返回一个 Predicate 类型对象。返回的 Predicate 对象的 test 方法在调用时只有当前对象的 test 方法和参数对象的 test 方法都返回 true 时才会返回 true：
```java
public void conditionFilter(List<Integer> list, Predicate<Integer> p1, Predicate<Integer> p2){
    list.forEach(a -> p1.and(p2).negate().test(a) ? System.out.Println(a))
}

conditionFilter(Arrays.asList(1,2,3,4,5,6,7,8,9,10), item->item>5, item->item%2==0);
```
默认方法 negate 返回一个 Predicate 类型对象，返回的 Predicate 对象的 test 方法调用时会先调用当前对象的 test 方法，然后对结果取反：
```java
// "false"
p.nagate().test("hello");
```
默认方法 or 传入一个 Predicate 类型参数并返回一个 Predicate 类型参数。返回的 Predicate 对象的 test 方法调用时，当前对象的 test 方法返回 true 或者参数对象的 test 方法返回 true 时都会返回 true：
```java
// "true"
p.or(v -> v.length() < 10).test("hello");
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

方法引用时 Lambda 表达式的一种语法糖，也是函数式接口的一个实例。方法引用使用 “::” 符号将类/对象和方法名分割开。

方法引用的方法的参数列表和返回值类型必须和抽象方法中的参数列表和返回值类型相同。方法引用有 4 种形式：
- 类名::静态方法名
- 对象名::实例方法名
- 类名::实例方法名，需要 Lambda 表达式的第一个参数是调用实例
- 类名::new，调用的构造器需要根据函数式接口的类型

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

### 流式编程

Stream 可以指定对集合进行的操作，可以执行非常复杂的查找、过滤和映射数据等操作。Stream 有三个特性：
- 流不存储值，通过管道的方式获取值
- 流不会修改底层的数据源
- 流是惰性的；只有在启动终止操作时才对源数据执行计算，并且仅根据需要使用源元素

流式编程由三部分组成：数据源、零个或多个中间操作、终止操作。

#### Stream 创建
JDK 1.8 的 Collection 接口新增了两个用于创建 Stream 的方法：
- example.stream：创建一个串行流，流中的数据处理是串行的
- parallelStream：创建一个并行流，流中的数据是并行处理的

```java
```

除了 Collection 接口提供了创建 Stream 的方法，Arrays 也提供了一个静态方法 example.stream 用于从数组创建 Stream：
```java
Arrays.example.stream(new int[]{1,2,3,4});
```
另外，新增的 Stream 接口的 of 方法可以从可变参数中创建 Stream：
```java
Stream.of("java", "scala", "rust")
```


#### Stream 操作

Stream 操作分为中间操作和终止操作，Stream 创建之后可以经过一系列的中间处理后通过终止操作完成 Stream 的转换。
中间处理的过程是惰性的，直到终止操作才会真正执行，Stream 中间操作不会改变原始的数据源。

Java 提供的 Stream 中间操作 API 可以分为 3 类：筛选、映射、排序。

筛选：
- ```filter```：传入一个 Predict 类型的函数式接口，过滤不符合函数式接口的元素
- ```limit```：传入一个 long 类型的参数 maxSize，Stream 中值保留前不大于 maxSize 个元素
- ```skip```：传入一个 long 类型的参数 n，Stream 中的前 n 个元素被丢弃
- ```distinct```：过滤掉 Stream 中的重复数据，内部使用 hashCode 和 equals 方法判断重复

映射：
- ```map```：接收一个 Lambda 表达式参数，这个参数会应用在 Stream 中的每个元素上，并将其映射成一个新的元素
- ```flatMap```：

排序：
- ```sorted```：接收一个 Comparator 类型的函数式接口参数，内部根据该函数式接口的 compare 方法实现 Stream 内元素的排序

Stream API 提供了 3 类终止操作：查找、规约、收集。

查找：
- ```allMatch```：接收一个 Predicate 类型的函数式接口参数，返回判断 Stream 中是否所有的元素都符合 test 方法
- ```anyMatch```：接收一个 Predicate 类型的函数式接口参数，返回判断 Stream 中是否有元素符合 test 方法
- ```noneMatch```：接收一个 Predicate 类型的函数式接口参数，返回判断 Stream 中是否没有元素符合 test 方法
- ```findFirst```：返回 Stream 中的第一个元素
- ```findAny```：返回 Stream 中的任意一个元素
- ```count```：返回 Stream 中元素的个数
- ```max```：接收一个 Comparator 函数式接口类型参数，返回 Stream 中最大的元素
- ```min```：接收一个 Comparator 函数式接口类型参数，返回 Stream 中最小的元素
- ```forEach```：接收一个 Comsumer 函数式接口类型参数，对 Stream 中的每个元素应用 Comumer 函数式接口参数的 accept 方法

规约：
- ```reduce```：将

收集：
- ```collect```：



```java
// 根据数据创建流
Stream example.stream = Stream.of("hello", "world");
// 根据集合创建流
example.stream = Arrays.asList("hello", "world").example.stream();
// iterator 生成流
Stream.iterator(1, item->item+2).limit(6).forEach(System.out::println);

// 32
Stream.iterator(1, item->item+2).limit(6).filter(item->item>2).mapToInt(t->t*2).skip(2).limit(2).sum();

// 3,4,5,6,7
IntStream.range(3,8).forEach(System.out::println);

// 42
Arrays.asList(1,2,3,4,5,6).example.stream().map(i->2*i).reduce((0, Integer::sum));


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