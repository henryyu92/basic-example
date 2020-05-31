package example;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.apache.dubbo.rpc.service.GenericService;

import java.util.HashMap;
import java.util.Map;

public class ConsumerMain {

    public static void main(String[] args) {
        genericService();
    }

    private static ApplicationConfig applicationConfig(){
        ApplicationConfig app = new ApplicationConfig();
        app.setName("dubbo-application");
        return app;
    }

    /**
     * 泛化调用 GenericService 用于消费者没有 API 接口，用于框架集成，其参数使用 Map 表示
     */
    public static void genericService(){

        // 全局配置
        ApplicationModel.getConfigManager().addConfig(applicationConfig());

        // 服务消费者配置
        ReferenceConfig<GenericService> referenceConfig = new ReferenceConfig<>();
        // 直连 provider
        referenceConfig.setUrl("dubbo://localhost:20880");

        // 远程调用的接口
        referenceConfig.setInterface("example.spi.HelloTest");
        referenceConfig.setVersion("1.0.0");
        // 设置泛化调用
        referenceConfig.setGeneric("true");

        // 远程调用
        GenericService genericService = referenceConfig.get();
        Object result = genericService.$invoke("hello", new String[]{String.class.getTypeName()}, new Object[]{"world"});
        System.out.println(result);

        Map<String, Object> person = new HashMap<>();
        person.put("name", "");
        person.put("age", "");
        Object hello = genericService.$invoke("hello", new String[]{Person.class.getName()}, new Object[]{
                person
        });
        System.out.println(hello);

    }

    private static class Person{
        String name;
        String age;
    }
}
