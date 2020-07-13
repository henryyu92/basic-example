package example.client;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.time.Duration;

public class Main {

    public static void main(String[] args) {
        model();
    }


    public static void model(){
        // 创建连接信息
        RedisURI uri = RedisURI.builder()
                .withHost("localhost")
                .withPort(6379)
                .withTimeout(Duration.ofSeconds(10))
                .build();
        // 创建客户端
        RedisClient client = RedisClient.create(uri);
        // 创建连接
        StatefulRedisConnection<String, String> connect = client.connect();
        // 创建同步命令
        RedisCommands<String, String> commands = connect.sync();
        // Set 命令参数
        SetArgs args = SetArgs.Builder.nx().ex(5);
        // 执行 Set 命令
        String res = commands.set("name", "hello world", args);

        System.out.println(res);

        String value = commands.get("name");
        System.out.println(value);

        // 关闭连接
        connect.close();
        // 关闭客户端
        client.shutdown();
    }
}
