package example.config;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.RegistryConfig;

public class ConfigFactory {

    public static ApplicationConfigBuilder newApplicationConfigBuilder(){
        return new ApplicationConfigBuilder();
    }

    public static RegistryConfigBuilder newRegistryConfigBuilder(){
        return new RegistryConfigBuilder();
    }

    /**
     * ApplicationConfig 配置类
     */
    public static class ApplicationConfigBuilder{

        private final ApplicationConfig application;

        private ApplicationConfigBuilder(){
            application = new ApplicationConfig();
        }

        public ApplicationConfigBuilder name(String name){
            application.setName(name);
            return this;
        }

        public ApplicationConfig build(){
            return application;
        }
    }

    /**
     * RegistryConfig 配置类
     */
    public static class RegistryConfigBuilder{
        private final RegistryConfig registry;

        private RegistryConfigBuilder(){
            registry = new RegistryConfig();
        }

        /**
         * 注册中心 id
         * @param id
         * @return
         */
        public RegistryConfigBuilder id(String id){
            registry.setId(id);
            return this;
        }

        /**
         * 注册中心地址
         * @param address
         * @return
         */
        public RegistryConfigBuilder address(String address){
            registry.setAddress(address);
            return this;
        }

        /**
         * 是否订阅服务
         * @param subscribe    false 表示不订阅服务，默认订阅服务
         * @return
         */
        public RegistryConfigBuilder subscribe(boolean subscribe){
            registry.setSubscribe(subscribe);
            return this;
        }

        /**
         * 是否注册服务
         * @param register      false 表示不注册服务，默认注册服务
         * @return
         */
        public RegistryConfigBuilder register(boolean register){
            registry.setRegister(register);
            return this;
        }
    }

    /**
     * MonitorConfig 配置类
     */
    public static class MonitorConfigBuilder{

    }
}
