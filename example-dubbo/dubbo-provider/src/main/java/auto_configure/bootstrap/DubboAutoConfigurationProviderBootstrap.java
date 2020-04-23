package auto_configure.bootstrap;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;

import java.util.concurrent.CountDownLatch;

/**
 * @author Administrator
 * @date 2019/6/7
 */


public class DubboAutoConfigurationProviderBootstrap {

    public static void main(String[] args) throws InterruptedException {
        ServiceConfig serviceConfig = new ServiceConfig();
        serviceConfig.setApplication(new ApplicationConfig("hello-demo"));
        serviceConfig.setRegistry(new RegistryConfig("zookeeper://192.168.119.129:2181"));
        serviceConfig.setProtocol(new ProtocolConfig("dubbo", 20880));

        serviceConfig.export();

        new CountDownLatch(1).await();
    }
}
