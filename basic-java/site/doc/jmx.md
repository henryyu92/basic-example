### JMX
JMX(Java Management Extensions) 是一个为应用程序植入管理功能的框架，用户可以在任何 Java 应用程序中使用这些代理和服务实现管理。

JMX 架构分为三层：
- 基础层：主要是 MBean，表示被管理的资源
- 适配层：MBeanServer 主要是提供对资源的注册和管理
- 接入层：提供远程访问的入口

MBean 主要使用三种：
- Standard MBean：这种类型的 MBean 能管理的资源必须定义在接口中，然后 MBean 必须实现这个接口，MBean 的命名也必须遵循规范
- Dynamic MBean：这种类型的 MBean 必须实现 ```javax.management.DynamicMBean``` 这个接口，所有的属性或方法都在运行时定义
- Model MBean：不需要写 MBean 类，只需要使用 ```javax.management.modelmbean.RequiredModelMBean``` 即可，管理的资源并不在 MBean 中而是在外部(通常是一个类)，只有在运行时使用 set 方法将其加入到 Model MBean 中

#### 基础层
Standard MBean 示例：
```java
// step 1: 定义接口，需要管理的资源必须定义在接口中，且接口名必须以 MBean 类名为前缀
public interface HelloMBean{
  String getName();
  void setName(String name);
  int getAge();
  void setAge(int age);
}

// step 2：定义实现类
public class Hello implements HelloMBean{
  private String name;
  private int age;
  public String getName(){
    return this.name;
  }
  public void setName(String name){
    this.name = name;
  }
  public int getAge(){
    return this.age;
  }
  public void setAge(int age){
    this.age = age;
  }
}

// step 3：定义 Agent 并注册 MBean
public void register(){
  MBeanServer server = ManagementFactory.getPlatformMBeanServer();
  // 创建 ObjectName，其中 jmxBean 和 hello 可以随意替换
  ObjectName name = new ObjectName("jmxBean:type=hello");
  server.registerMBean(new Hello, name);
}

// step 4：在 JAVA_HOME\bin 下打开 jvisualvm.exe 可以看到被管理的 MBean，注意可能需要安装 MBean 相关插件才能看到
```
Dynamic MBean 示例：
```java
public class JmxReport{

  // step 1 实现 DynamicMBean
  private static class KafkaMbean implements DynamicMBean{
    private final ObjectName objectName;
	private final Map<String, KafkaMetric> metrics;
	
	public KafkaMbean(String mbeanName){
	  this.objectName = new ObjectName(mbeanName);
	  this.objectName = new ObjectName(mbeanName);
	}
	
	public ObjectName name(){
	  return objectName;
	}
	
	@Override
	public Object getAttribute(String name) throws AttributeNotFoundException, MBeanException, ReflectionException {
		if (this.metrics.containsKey(name))
			return this.metrics.get(name).metricValue();
		else
			throw new AttributeNotFoundException("Could not find attribute " + name);
	}
  }
  // step 2：注册服务
  public void register(KafkaMbean mbean){
    MBeanServer server = ManagementFactory.getPlatformMBeanServer();
	try{
	  if (server.isRegistered(mbean.name()))
	    server.unregisterMBean(mbean.name());
	  server.registerMBean(mbean, mbean.name());
	}catch (JMException e){
	  throw new KafkaException("Error registering mbean " + mbean.name(), e);
	}
	
  }


}
```
#### 适配层
#### 接入层
使用 jvisualvm 可以查看并修改被管理的资源，也可以自定义实现接入层，只需要在 Agent 层做一些修改即可：
```java
Boolean.parseBoolean(System.getProperty("com.sun.management.jmxremote.ssl","false"));
Boolean.parseBoolean(System.getProperty("com.sun.management.jmxremote.authenticate", "false"));
int port = Integer.parseInt(System.getProperty("com.sun.management.jmxremote.port", "-1"));

MBeanServer server = ManagementFactory.getPlatformMBeanServer();
ObjectName name = new ObjectName("jmxBean:type=hello");
server.registerMBean(new Hello, name);

try{
  // 注册一个端口，绑定 url 后客户端通过 rmi 方式连接 JMXConnectorServer
  LocateRegistry.createRegistry(rmiRegistryPort);
  // 指定 URL
  JMXServerURL url = buildJMXServiceURL(rmiRegistryPort, port);
  JMXConnectorServer jcs = JMXConnectorServerFactory.newJMXConnectorServer(serviceUrl, jmxEnv, mbs);
  jcs.start();
}

public static JMXServiceURL buildJMXServiceURL(int rmiRegistryPort, int rmiConnectorPort){
  StringBuilder url = new StringBuilder();
  url.append("service:jmx:rmi://localhost:")
    .append(rmiConnectorPort)
	.append("/jndi/rmi://localhost:")
	.append(rmiRegistryPort)
	.append("/jmxrmi");
  return new JMXServiceURL(url.toString());
}

// service:jmx:rmi://hostname1:port1/jndi/rmi://hostname2:port2/jmxrmi
// service:jmx:rmi 表示 JMX 服务使用 rmi 协议进行通信
// hostname1:port1 表示 JMX 服务对外的主机和端口，其中主机一般不用
// jndi 表示使用 jddi 服务对 JMX 服务进行注册
// rmi://hostname2:port2 表示 RMI Registry 的访问地址，RMI Registry 只能和 JXM 服务在同一台机器上，因此 hostname2 只能是 JMX 服务的主机名
// jmxrmi 表示 RMI Registry 中注册的服务名
```
创建 Client 监控被管理的 MBean：
```java
JMXServiceURL url = new JMXServiceURL("service:jmx:rmi://localhost:8888/jndi/rmi://localhost:9999/jmxrmi");
JMXConnector jmxc = JMXConnectorFactory.connect(url, jmxEnv);
MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
ObjectName mbeanName = new ObjectName("jmxBean:type=hello");
// 设置属性
mbsc.setAttribute(mbeanName, new Attribute("Name", "haha"));
// 获取属性
String name = (String)mbsc.getAtrribute(mbeanName, "Name");
// 使用代理
HelloBean proxy = MBeanServerInvocationHandler.newProxyInstance(mbsc, mbeanName, HelloBean.class, false);
proxy.helloWorld();
// 使用反射只能调用非属性方法
mbsc.invoke(mbeanName, "helloWorld", null, null);
```
#### Notification
Notification 作为 MBean 之间沟通的桥梁，有四部分组成：
- Notification：相当于一个信息包封装了需要传递的信息
- NotificationBroadcaster：相当于一个广播器用于把消息广播出去
- NotificationListener：用于监听广播出来的通知消息
- NotificationFilter：用于过滤掉不需要的通知
```java
public class HelloListener implements NotificationListener{
  public void handleNotification(Notification notify, Object handback){
    if(handback instanceof Hello){
	  System.out.println(notify.getMessage());
	}
  }
}
public class Jack extend NotificationBroadcasterSupport implements JackMBean{
  public void hi(){
    Notification notify = new Notification("jack hi", 1, System.currentTimeMillis(), "jack");
	sendNotification(notify);
  }
  
  public void register(){
     MBeanServer server = ManagementFactory.getPlatformMBeanServer();
	 server.registerMBean(jack, new ObjectName("jack:name=Jack"));
     jack.addNotificationListener(new HelloListener(), null, hello);
     Thread.sleep(500000);
  }
}
```

**[Back](../)**