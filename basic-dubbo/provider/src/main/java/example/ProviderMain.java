package example;

import example.spi.HelloTest;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.rpc.service.GenericException;
import org.apache.dubbo.rpc.service.GenericService;

public class ProviderMain {

    public static void main(String[] args) {

    }


    /**
     * 泛化实现用于服务提供方没有接口实现类，参数使用 Map 表示，泛化实现可用于 Mock 测试，即使用一个泛化实现代替正真的实现
     *
     * 泛化实现类需要实现 GenericService 接口
     */
    public static void genericService(){

        ServiceConfig<GenericServiceImpl> service = new ServiceConfig<>();
        service.setId("genericService");
        service.setVersion("1.0.0");
        service.setInterface(HelloTest.class.getName());
        service.setRef(new GenericServiceImpl());

        service.export();
    }

    static class GenericServiceImpl implements GenericService{
        @Override
        public Object $invoke(String method, String[] parameterTypes, Object[] args) throws GenericException {

            if ("hello".equalsIgnoreCase(method)){
                return "hello " + args[0];
            }

            return args[0];
        }
    }
}
