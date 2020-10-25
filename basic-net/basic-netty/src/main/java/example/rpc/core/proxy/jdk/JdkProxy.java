package example.rpc.core.proxy.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * JDK 动态代理生成的代理类会继承自定义的 InvocationHandler，因此只有实现接口才能实现代理
 */
public class JdkProxy implements InvocationHandler {

    private Object target;

    public JdkProxy(Object target){
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println(proxy.getClass().getName());
        return method.invoke(target, args);
    }
}
