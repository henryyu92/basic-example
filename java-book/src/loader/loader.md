## 类加载器
对于任意一个类，都需要由加载它的类加载器和这个类本身一同确立其在 Java 虚拟机中的唯一性，每一个类加载器都拥有一个独立的类名称空间。因此两个类即使来源于同一个 Class 文件，只要类加载器不同那么这两个类就必定不相等，包括 Class 对象的 equals 方法、isInstance 方法。
### 系统类加载器
- **启动类加载器(Bootstrap ClassLoader)**：负责将存放在 ```JAVA_HOME\lib``` 目录中或者被 ```-Xbootclasspath``` 参数所指定的路径中并且是虚拟机识别的类库加载到虚拟机内存中，由虚拟机实现(C++ 语言)，不能直接引用。
- **扩展类加载器(Extension ClassLoader)**：负责加载 ```JAVA_HOME\lib\ext``` 目录或者 ```java.ext.dirs``` 指定的路径中的所有类库，由 ```sun.misc.Laucher$ExtClassLoader``` 实现，可以直接引用。
- **应用程序类加载器(Application ClassLoader)**：负责加载用户类路径(classpath)上所指定的类库，由 ```sun.misc.Laucher$AppClassLoader``` 实现，也是 ```getSystemClassLoader()```的返回值，可以直接引用。
### 双亲委派模型
双亲委派模型要求除了顶层的启动类加载器外，其他的类加载器都应当有自己的父类加载器，这里的父子关系是以组合的方式实现。

双亲委派模型的工作过程：**如果一个类加载器收到了类加载的请求，它首先不会自己去尝试加载这个类，而是把这个请求委派给父类加载器去完成，因此所有的加载请求最终都应该传送到顶层的启动类加载器中，只有当父类加载器反馈自己无法完成这个加载请求(搜索范围内没有找到所需的类)时，子类加载器才会尝试自己去加载。**

双亲委派模型的实现集中在```java.lang.ClassLoader#loadClass()```方法，因此自定义类加载器的时候只需要继承```java.lang.ClassLoader```类并重写```findClass()```方法即可：
```java
protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    // 检查类是否已经被加载过
    Class<?> c = findLoadedClass(name)
    if (c == null) {
        try {
            if (parent != null) {
                // 父类加载器加载
                c = parent.loadClass(name, false);
            } else {
                // Bootstrap 类加载器加载
                c = findBootstrapClassOrNull(name);
            }
        } catch (ClassNotFoundException e) {
            // 父类加载器无法完成加载
        }
        if (c == null) {
            c = findClass(name);
        }

    }
}

protected Class<?> findClass(String name) throws ClassNotFoundException {
    throw new ClassNotFoundException(name);
}
```
### 线程上下文类加载器
Java 提供一个线程上下文类加载器(Thread Context ClassLoader)，这个类加载器可以通过```java.lang.Thread#setContextClassLoader()```方法设置，如果线程创建时还未设置则从父线程继承一个，如果在应用程序的全局范围内都没有设置则这个类加载器就是应用程序类加载器(AppClassLoader)。使用```Thread.currentThread().getContextClassLoader()``` 获取线程上下文类加载器，Java SPI 机制就是采用线程上下文类加载器实现。
```java
public static <S> ServiceLoader<S> load(Class<S> service) {
  ClassLoader cl = Thread.currentThread().getContextClassLoader();
  return ServiceLoader.load(service, cl);
}
```
#### SPI
SPI(Service Provider Interface) 是一种服务提供发现机制，用在不同模块间通过接口调用服务，避免对具体服务接口实现类的耦合。Java SPI 的使用步骤：
- 服务调用方通过 ```ServiceLoader#load``` 加载服务接口的实现类实例
- 服务提供方实现服务接口后，在 META-INF/services 目录下新建一个接口全限定名的文件并将具体实现类全限定名写入

服务调用方定义接口，使用 ```ServiceLoader#load``` 方法加载实现接口的服务：
```java
package org.test;

public interface HelloService{
    void sayHello();
}

ServiceLoader<HelloService> loaders = ServiceLoader.load(HelloService.class);
// Java SPI 会加载所有所有的实现
for(HelloService hello : loaders){
    hello.sayHello();
}
```
服务提供方实现接口并在 META-INF/services 目录下新建文件：
```java
package org.testimpl;

import org.test.HelloService;

public class HelloServiceImpl implements HelloService{
    public void sayHello(){
	    println("hello world !");
	}
}

// META-INFO/services/org.test.HelloService 文件下添加实现类全限定名
org.testimpl.HelloServiceImpl
```

**[Back](../../)**