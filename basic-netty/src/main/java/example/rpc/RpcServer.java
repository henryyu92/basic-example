package example.rpc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RpcServer {

    /**
     * 处理请求线程池
     */
    private static final ExecutorService pool = Executors.newFixedThreadPool(10);
    /**
     * 注册服务
     */
    private static final Map<String, List<Class>> registry = new ConcurrentHashMap<>(16);

    /**
     * 服务端启动时：
     * ·注册服务
     * ·监听端口
     */
    public void start() throws IOException {

        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress("localhost", 6666));
        while (true) {
            pool.execute(new ServiceTask(serverSocket.accept()));
        }

    }

    class ServiceTask implements Runnable {

        private Socket socket;

        public ServiceTask(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                // 反序列化
                String serviceName = in.readUTF();
                String methodName = in.readUTF();
                Class<?>[] parameterTypes = (Class<?>[]) in.readObject();
                Object[] args = (Object[]) in.readObject();

                // 选择服务
                Class serviceImpl = registry.get(serviceName).get(0);
                // 反射调用服务
                Method method = serviceImpl.getMethod(methodName, parameterTypes);
                Object result = method.invoke(serviceImpl.newInstance(), args);

                // 结果序列化
                try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
                    out.writeObject(result);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void registServeice(Class serviceInterface, Class impl) {
        synchronized (registry) {
            List<Class> classes = registry.get(serviceInterface.getName());
            if (classes == null || classes.size() <= 0) {
                classes = new ArrayList<>();
                classes.add(impl);
                registry.put(serviceInterface.getName(), classes);
            } else {
                classes.add(impl);
            }
        }
    }
}
