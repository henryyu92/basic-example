## SPI

SPI 是 Java 提供的一种服务发现机制，通过在 META-INF/services 文件夹中创建接口全限定名的文件，文件内容是接口实现类的全限定名，使用 ServiceLoader#load 方法就可以将在文件中所有的定义的实现类。

Java SPI 会加载文件中所有的类，Dubbo 没有使用 Java 的 SPI 机制，而是自定义了类加载机制加载指定的类。Dubbo SPI 机制是 Dubbo 可扩展的基础，通过 Dubbo SPI 机制，可以自定义各种处理。

Dubbo SPI 机制的实现在 ExtensionLoader 类中，ExtensionLoader 在 META-INF/dubbo 文件夹下加载接口全限定名对应的文件中指定的类。

```
// META-INF/dubbo 路劲的  org.example.HelloTest 文件中

hello = example.spi.HelloTestImpl

// 测试类
ExtensionLoader<HelloTest> loader = ExtensionLoader.getExtensionLoader(HelloTest.class);
HelloTest hello = loader.getExtension("hello");
hello.hello("world")
```

```ExtensionLoader#getLoader``` 方法获取扩展类对象，方法首先从缓存中获取对应的扩展类，如果没有命中则创建一个新的扩展类。