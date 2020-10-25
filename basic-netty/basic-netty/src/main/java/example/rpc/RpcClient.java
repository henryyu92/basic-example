package example.rpc;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

public class RpcClient<T> {

    // stub
    public T getStub(final Class<?> serviceInterface, final InetSocketAddress address){
        return (T) Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class[]{serviceInterface}, (proxy, method, args) -> {
            try(Socket socket = new Socket()){
                // 创建连接
                socket.connect(address);
                // 序列化请求
                try(ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())){
                    out.writeUTF(serviceInterface.getName());
                    out.writeUTF(method.getName());
                    out.writeObject(method.getParameterTypes());
                    out.writeObject(args);
                }catch (Exception e){
                    e.printStackTrace();
                }
                // 反序列化响应数据
                try(ObjectInputStream in = new ObjectInputStream(socket.getInputStream())){
                    return in.readObject();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        });
    }

}
