## Lettuce
Lettuce 是一个可扩展的线性安全的 Redis 客户端，提供同步、异步和反应式 API。Lettuce 可以让多个非事务操作的线程共用一个连接，Lettuce 底层使用 Netty 来管理连接；Lettuce 支持 Sentinel 和 Cluster 等 Reids 高级功能。
### 基本使用
```java
DefaultClientResources resources = DefaultClientResources.create();
RedisURI redisURI = RedisURI.builder().withHost(properties.getHost()).withPort(properties.getPort()).build();
ReidsClient client = RedisClient.create(resources, redisURI);
StatefulRedisConnection<String, String> connect = redisClient.connect();
RedisCommands<String, String> commands = connection.sync();

...

connection.close();
resources.shutdown();
client.shutdown();
```
### 异步连接
```java
RedisClient client = RedisClient.create("redis://localhost");
RedisAsyncCommands<String, String> commands = client.connect().async();
```
### Redis 集群
```java
RedisURI redisUri = RedisURI.Builder.redis("localhost").withPassword("authentication").build();

RedisClusterClient clusterClient = RedisClusterClient.create(redisUri);
StatefulRedisClusterConnection<String, String> connection = clusterClient.connect();
RedisAdvancedClusterCommands<String, String> syncCommands = connection.sync();

...

connection.close();
clusterClient.shutdown();
```

定期拓扑更新
```java
RedisURI redisUri = RedisURI.Builder.redis("localhost").withPassword("authentication").build();

RedisClusterClient clusterClient = RedisClusterClient.create(redisUri);
StatefulRedisClusterConnection<String, String> connection = clusterClient.connect();
RedisAdvancedClusterCommands<String, String> syncCommands = connection.sync();

...

connection.close();
clusterClient.shutdown();
```

自适应拓扑更新
```java
RedisURI node1 = RedisURI.create("node1", 6379);
RedisURI node2 = RedisURI.create("node2", 6379);

RedisClusterClient clusterClient = RedisClusterClient.create(Arrays.asList(node1, node2));

ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                                .enableAdaptiveRefreshTrigger(RefreshTrigger.MOVED_REDIRECT, RefreshTrigger.PERSISTENT_RECONNECTS)
                                .adaptiveRefreshTriggersTimeout(30, TimeUnit.SECONDS)
                                .build();

clusterClient.setOptions(ClusterClientOptions.builder()
                                .topologyRefreshOptions(topologyRefreshOptions)
                                .build());
...

clusterClient.shutdown();
```
### Spring 集成
单节点
```java
@Configuration
public class LettuceConfig {

    @Bean(destroyMethod = "shutdown")
    ClientResources clientResources() {
        return DefaultClientResources.create();
    }

    @Bean(destroyMethod = "shutdown")
    RedisClient redisClient(ClientResources clientResources) {

        return RedisClient.create(clientResources, RedisURI.create(TestSettings.host(), TestSettings.port()));
    }

    @Bean(destroyMethod = "close")
    StatefulRedisConnection<String, String> connection(RedisClient redisClient) {
        return redisClient.connect();
    }
}
```

集群
```java
@Configuration
public class LettuceConfig {

    @Bean(destroyMethod = "shutdown")
    ClientResources clientResources() {
        return DefaultClientResources.create();
    }

    @Bean(destroyMethod = "shutdown")
    RedisClusterClient redisClusterClient(ClientResources clientResources) {

        RedisURI redisURI = RedisURI.create(TestSettings.host(), 7379);

        return RedisClusterClient.create(clientResources, redisURI);
    }

    @Bean(destroyMethod = "close")
    StatefulRedisClusterConnection<String, String> clusterConnection(RedisClusterClient clusterClient) {
        return clusterClient.connect();
    }
}
```


https://www.cnblogs.com/throwable/p/11601538.html

**[Back](../)**