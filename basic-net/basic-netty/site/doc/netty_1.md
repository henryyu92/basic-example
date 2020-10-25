Netty 是一个异步的、基于事件驱动的网络应用框架，其本质是一个 NIO 框架，广泛应用于分布式系统中的 RPC 框架。


## Unix 网络I/O模型
根据 Linux 网络编程对 I/O 模型的分类，UNIX 提供了 5 中 I/O 模型，以套接字(Socket)接口为例：
- **阻塞 I/O 模型**  
 
![](assets/bio.png)
- 非阻塞 I/O 模型  
 
![](assets/nio.png)
- I/O 复用模型  
 
![](assets/mio.png)
- 信号驱动 I/O 模型  
 
![](assets/sio.png)
- 异步 I/O 模型  
告知内核启动某个操作，并让内核在整个操作结束后(包括将数据从内核复制到应用进程缓冲区)通知应用进程。  
异步 I/O 模型与信号驱动模型的区别是：信号驱动 I/O 模型由内核通知应用进程何时可以开始 I/O 操作，异步 I/O 模型由内核通知应用进程 I/O 操作何时已经完成。  
![](assets/aio.png)
### 用户空间和内核空间
现代操作系统都是采用虚拟存储器，对于 32 位操作系统而言寻址空间(虚拟存储空间)为 4G(2 的 32 次方)。操作系统的核心是内核，独立与普通的应用程序，可以访问受保护的内存空间，也有访问底层硬件设备的所有权限。为了保证用户进程不能直接操作内核，操作系统将虚拟空间划分为内核空间和用户空间。
### I/O 多路复用技术
在 I/O 编程过程中，当需要同时处理多个客户端连接请求时，I/O 多路复用技术通过把多个 I/O 的阻塞复用到同一个 select 的阻塞上，从而使得系统可以在单线程的情况下可以处理多个客户端的请求。与传统的多线程/多进程模型相比，I/O 多路复用模型不需要系统创建额外的进程和线程，也不需要维护这些线程和进程的运行，降低了系统的维护工作量，节省了系统资源。I/O 多路复用模型的使用场景：
- 服务器需要同时处理多个处于监听状态或者多个连接状态的套接字
- 服务器需要同时处理多种网络协议的套接字

为了克服 select 的缺陷，Linux 在新的内核版本中选择 epoll 作为替代，相较于 select，epoll 做了许多重大改进：
- 支持一个进程打开的 socket 描述符(fd)不受限制(仅受限于操作系统的最大文件句柄数)  
select 单进程默认单个进程只能打开 1024 个 fd，当连接数超过此限制时，就必须等待；可以采用多进程来解决这个问题，但是多进程之间的数据交换比较麻烦，因此也不是很好的方案；epoll 单进程没有这个限制，因此效率会更好  
- I/O 效率不会随着 fd 数量的增加而线性下降  
当拥有一个很大的 socket 集合时，由于网络延时或者链路空闲，任何时刻只有少部分socket 时活跃的，但是 select/poll 每次调用会轮询扫描整个集合，导致效率线性下降；epoll 克服了这个问题，因为在内核实现中，epoll 是根据每个 fd 上的 callback 函数实现的，只有 socket 是活跃的才会调用 callback，因此效率不会随着 fd 数量线性下降。
- 使用 mmap 加速内核与用户空间的消息传递  
epoll 通过内核和用户空间 mmap 同一块内存来实现避免内核和用户空间之间的 fd 消息复制。
## TCP 协议
- TCP 提供一种面向连接的、可靠的字节流服务
- TCP 使用校验和、确认和重传机制来保证可靠传输
- TCP 给数据字节排序，并使用累积确认保证数据的顺序不变和非重复
- TCP 使用滑动窗口机制来实现流量控制，通过动态改变窗口大小进行拥塞控制

TCP 不会在字节流中插入记录标识符，接收方只能每次接收窗口内的数据(半包问题)；TCP 对字节流的内容不做任何解释，对字节流的解释有应用层完成。
### TCP 三次握手
所谓三次握手是指建立一个 TCP 连接时，需要客户端和服务端总共发送 3 个数据包。三次握手的目的是连接服务器指定端口，建立 TCP 连接，并同步连接双方的序列号和确认号，交换 TCP 窗口大小信息：
- 第一次握手(SYN=1, seq=X)：客户端发送一个 TCP 的 SYN 标志位为 1 的包，指明客户端打算连接的服务器的端口以及初始序列号 seq 为 X，发送完毕后客户端进入 SYN_SEND 状态
- 第二次握手(SYN=1, ACK=1, seq=X+1, ACKnum=X+1)：服务器返回确认包应答，即 SYN 标志位和 ACK 标志位都为 1，seq 为服务器自己的序列号 Y，同时将确认序列号(Acknowledgement Number) ACKnum 设置为 X+1，发送完毕后服务器进入 SYN_RCVD 状态
- 第三次握手(ACK=1, ACKnum=y+1)：客户端再次发送确认包，SYN 标志位设置为 0，ACK 标志位设置为 1，ACKnum=y+1，发送完毕后客户端进入 ESTABLISHED 状态，当服务器收到这个包时也进入 ESTABLISHED 状态，TCP 握手结束
### TCP 四次挥手
TCP 断开连接需要发送四个数据包，也叫四次挥手，客户端和服务器均可主动发起挥手动作：
- 第一次挥手(FIN=1, seq=x)：客户端发送 FIN 为 1 的数据包，表示自己已经没有数据可以发送了，但是仍然可以接收数据，发送完毕后进入 FIN_WAIT_1 状态
- 第二次挥手(ACK=1, ACKnum=x+1)：服务器发送确认包，表明自己接收到了客户端的关闭连接请求，但还没有准备好关闭连接，发送完毕后服务器进入 CLOSE_WAIT 状态；客户端接收到确认包之后进入 FIN_WAIT_2 状态等待服务器关闭连接
- 第三次挥手(FIN=1, seq=y)：服务器准备好关闭连接时，向客户端发送结束连接请求，发送完毕后服务器进入 LAST_ACK 状态，等待来自客户端的最后一个 ACK
- 第四次挥手(ACK=1, ACKnum=y+1)：客户端接收到来自服务器的关闭请求，发送一个确认包并进入 TIME_WAIT 状态，等待可能出现的要求重传的 ACK 包；服务器端接收到这个确认包之后关闭连接进入 CLOSED 状态，客户端在等待了两个最大段生命周期之后没有收到服务器端的 ACK 任务服务器已经正常关闭连接，于是也关闭连接进入 CLOSED 状态

### TCP keepAlive
TCP 通信双方建立交互的连接，但是并不是一直存在数据交互，有些连接在数据交互完毕后会主动释放连接；在出现机器掉电、死机等意外会使得大量没有释放的 TCP 连接浪费系统资源。

TCP KeepAlive 的基本原理是：每隔一段时间给链接对端发送一个探测包，如果收到对方回应的 ACK，则认为连接还是存活的，在超过一定重试次数之后还是没有收到对方的回应，则丢弃该 TCP 连接。
## Java 网络编程
### BIO 编程
同步阻塞编程模型中， ServerSocket 负责绑定 IP 地址并启动监听端口；Socket 负责发起连接操作；连接成功后客户端和服务器通过输入和输出流进行同步阻塞式通信。  
#### 通信模型
采用 BIO 通信模型的服务端，通常由一个独立地 Acceptor 线程负责监听客户端的连接，它收到连接请求后为每一个客户端创建一个线程进行链路处理，处理完之后通过输出流应答客户端，然后销毁线程，这就是典型的一请求一应答的通信模型。  
![](assets/bio.png)
#### BIO 服务端
服务端在启动时启动 Acceptor 线程监听客户端的连接，当建立连接后创建新的线程处理客户端的请求：
```java
public class TimeServer{

  public static void main(String[] args){
    startAcceptor();
  }

  public static void startAcceptor(){
    new Thread("Acceptor Thread"){
	  public void run(){
	    ServerSocket server = null;
		try{
		  // 创建 ServerSocket 并绑定监听端口
		  server = new ServerSocket(port);
		  Socket socket = null;
		  while (true){
			// 监听端口阻塞等待客户端的连接
			socket = server.accept();
			new Thread(new TimeServerHandler(socket), "handlerThread").start();
		  }
		}finally {
		  if (server != null){
			server.close();
		  }
		}
	  }
	}
  }
```

```java
public class TimeServerHandler implements Runnable {
    private Socket socket;
    public TimeServerHandler(Socket socket){
        this.socket = socket;
    }
    public void run(){
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            String currentTime = null;
            String body = null;
            while (true){
                body = in.readLine();
                if (body == null){
                    break;
                }
                System.out.println("The time server recieve order: " + body);
                currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
                out.println(currentTime);
            }
        }catch (Exception e){
            if (socket != null){
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
  }
}

```
##### BIO 客户端
```java
public calss TimeClient{
    public void connect(int port){
        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            // 连接服务端
            socket = new Socket("127.0.0.1", port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("QUERY TIME ORDER");
            System.out.println("Send order 2 server succeed.");
            String resp = in.readLine();
            System.out.println("Now is: " + resp);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            // 关闭 socket 时会自动关闭 inputStream 和 outputStream
            if (socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
```
BIO 编程模型的主要问题在于每有一个客户端请求接入时，服务器就需要创建一个线程区处理新接入的客户端链路，当有大量客户端时服务器就会频繁的创建线程和销毁线程，这样会严重影响性能。
### 伪异步编程
同步阻塞 I/O 每个链路都需要创建线程，处理完之后销毁线程，这样频繁的创建和销毁线程严重影响性能，因此可以引入线程池技术，避免线程频繁创建和销毁。  
#### 通信模型
当有新的客户端接入时，将客户端的 Socket 封装成一个 Task 投递到线程池中进行处理，由于线程池的消息队列大小和活跃线程数是可以设置的，因此可以一定程度的避免线程的频繁创建和销毁。  
![](asset/bio2.png)
伪异步编程只需要在服务端代码使用线程池取代创建线程来处理任务：
```java
public class TimeServer{

    public void start(){
        int port = 8080;
        ServerSocket server = null;
        try{
            server = new ServerSocket(port);
            Socket socket = null;
            TimeServerHandlerExecutePool singleExecutor = new TimeServerHandlerExecutorPool(50, 10000);
            while(true){
                socket = server.accept();
                singleExecutor.execute(new TimeServerHandler(socket));
            }
        }finally{
            if(server != null){
                server.close();
                server = null;
            }
        }
    }

    private static class TimeServerHandlerExecutorPool{
        private ExecutorService executor;
        public TimeServerHandlerExecutorPool(int maxPoolSize, int queueSize){
            executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), maxPoolSize, 120L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runable>(queueSize))
        }
        public void execute(Runnable task){
            executor.execute(task);
        }
    }
}
```
伪异步 IO 模型使用了线程池技术，但是当客户端和服务器维持长连接时，由于连接不释放导致消息队列上累积的连接请求越来越多从而产生大量的连接超时；再者由于底层还是使用的阻塞式通信模型，因此效率依然
### NIO 编程
与 ServerSocket 和 Socket 相对应， Nio 提供了 SocketChannel 和 ServerSocketChannel 两种不同的套接字通道实现，这两种通道都支持阻塞和非阻塞模式。
#### 核心类
- Buffer  
Buffer 是一个对象包含一些要写入或者读出的数据，NIO 中所有的数据都是用 Buffer 处理的。读数据时直接从 Buffer 中读，写数据时直接写到 Buffer 中。每一种 JAVA 数据类型都对应着一种 Buffer，如 ByteBuffer, StringBuffer 等；  
BUffer 维护着几个核心属性：capacity、position、limit
- Channel  
Channel 是一个通道，网络数据到时通过 Channel 读取和写入；通道与流的不同之处在于通道是双工的可以用于读、写或者二者同时进行，而流只是在一个方向上移动。Channel 主要分为网络读写的 SelectableChannel 和文件读写的 FileChannel;
- Selector  
多路复用器提供选择已经就绪的任务的能力，Selector 会不断地轮询注册在它上面的 Channel，如果某个 Channel 上面发生读或者写事件，这个 Channel 就处于就绪状态并被 Selector 轮询出来，通过 SelectionKey 可以获取就绪的 Channel 集合进行后续的 I/O 操作。一个 Selector 可以同时轮询多个 Channel，JDK 使用 epoll 代替 select 实现因此 Selector 的心能不会很差，这样只需要一个 Selector 线程负责轮询所有的 Channel 其他线程处理 I/O 操作就可以接入大量的客户端而不会影响性能。
#### 服务器代码示例
```java
public class TimeServer{
    public void start(){
        int port = 8080;
        MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
        new Thread(timeServer, "Nio-MultiplexerTimeServer").start();
    }

    private static class MultiplexerTimeServer implements Runnable{
        private Selector selector;
        private ServerSocketChannel serverChannel;
        private volatile boolean stop;

        public MultiplexerTimeServer(int port){
            try{
                // step 1：打开 ServerSocketChannel 用于监听客户端的连接
                ServerSocketChannel acceptorSvr = ServerSocketChannel.open();
                // step 2：绑定监听端口，设置为非阻塞模式
                acceptorSvr.socket().bind(new InetSocketAddress(port), 1024);
                acceptorSvr.configureBlocking(false);
                // step 3：打开 Selector
                Selector selector = Selector.open();
                // step 4：将 ServerSocketChannel 注册到 Reactor 线程的多路复用器 Selector 上，监听 Accept 事件
                acceptorSvr.register(selector, SelectionKey.OP_ACCEPT, ioHandler);
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        public void stop(){
            this.stop = stop;
        }
        @Override
        public void run() {
            while (!stop){
                try{
                    // step 5：Selector 轮询准备就绪的 Key，轮询到的 Key 放入 SelectedKeys
                    selector.select(1000);
                    // 获取准备就绪的 Key
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> it = selectionKeys.iterator();
                    SelectionKey key = null;
                    while (it.hasNext()){
                        key = it.next();
                        it.remove();
                        try{
                            handleInput(key);
                        }catch (Exception e){
                            if (key != null){
                                key.cancel();
                                if (key.channel() != null){
                                    key.channel().close();
                                }
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if (selector != null){
                try{
                    selector.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()){
            // step 6：客户端连接请求，完成 TCP 三次握手建立物理链路
            if (key.isAcceptable()){
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                // step 7：建立 Socket 连接
                SocketChannel sc = ssc.accept();
                // 设置 SocketChannel 为非阻塞模式
                sc.configureBlocking(false);
                // step 8：将 SocketChannel 注册到 Selector 上监听读操作
                sc.register(selector, SelectionKey.OP_READ);
            }
            if (key.isReadable()){
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                // step 9：异步读取请求消息到缓冲区
                int readBytes = sc.read(readBuffer);
                if (readBytes > 0){
                    // limit = position
                    // position = 0
                    // mark = -1
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes, "utf-8");
                    System.out.println("The time server receive order: " + body);
                    String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
                    doWrite(sc, currentTime);
                }
                if(readBytes < 0){
                    key.cancel();
                    sc.close();
                }

            }
        }
    }

    private void doWrite(SocketChannel channel, String response) throws IOException {
        if (response != null && response.trim().length() > 0){
            byte[] bytes = response.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();
            channel.write(writeBuffer);
        }
    }
    }
}

```
#### 客户端代码示例
```java
public class TimeClient{
    public void start(){
        int port = 8080;
        new Thread(new TimeClientHandler("127.0.0.1", port), "TimeClientHandler");
    }

    static class TimeClientHandler implements Runnable {

        private String host;
        private int port;
        private Selector selector;
        private SocketChannel socketChannel;
        private volatile boolean stop;

        public TimeClientHandler(String host, int port){
            this.host = host;
            this.port = port;
            try{
                // step 1：打开 SocketCahnnel
                socketChannel = SocketChannel.open();
                // step 2：设置 SocketChannel 为非阻塞模式
                socketChannel.configureBlocking(false);
                // 打开 Selector
                selector = Selector.open();
            }catch (Exception e){
                e.printStackTrace();
                System.exit(1);
            }
        }

        @Override
        public void run() {

            try{
                doConnect();
            }catch (IOException e){
                e.printStackTrace();
                System.exit(1);
            }
            while (!stop){
                try{
                    // Slector 开始轮询
                    selector.select(1000);
                    // 准备就绪的 Channel 放入 SelectionKeys
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> it = selectionKeys.iterator();
                    SelectionKey key = null;
                    while (it.hasNext()){
                        key = it.next();
                        it.remove();
                        try{
                            handleInput(key);
                        }catch (Exception e){
                            if (key != null){
                                key.cancel();
                                if (key.channel() != null){
                                    key.channel().close();
                                }
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    System.exit(1);
                }
            }
            if (selector != null){
                try{
                    selector.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        private void handleInput(SelectionKey key) throws IOException {
            if (key.isValid()){
                SocketChannel sc = (SocketChannel) key.channel();
                // 连接事件则注册 读 SocketChannel 的事件到 Selector 上
                if (key.isConnectable()){
                    // 有连接事件时对连接结果进行判断
                    if (sc.finishConnect()){
                        sc.register(selector, SelectionKey.OP_READ);
                        doWrite(sc);
                    }else {
                        System.exit(1);
                    }
                }
                // 读事件直接读取数据
                if (key.isReadable()){
                    ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                    int readBytes = sc.read(readBuffer);
                    if (readBytes > 0){
                        readBuffer.flip();
                        byte[] bytes = new byte[readBuffer.remaining()];
                        readBuffer.get(bytes);
                        String body = new String(bytes, "utf-8");
                        System.out.println("now is: " + body);
                        this.stop = true;
                    }
                    if (readBytes < 0){
                        key.cancel();
                        sc.close();
                    }
                }
            }
        }
        // 客户端异步连接 Server 并将 SocketChannel 注册到 Selector 上监听读事件
        private void doConnect() throws IOException {
            if (socketChannel.connect(new InetSocketAddress(host, port))){
                socketChannel.register(selector, SelectionKey.OP_READ);
                doWrite(socketChannel);
            }else {
                // 如果没有返回成功则说明没有返回 TCP 握手应答消息但是并不意味着连接失败，所以需要注册连接事件，如果连接事件就绪则重新注册读事件
                socketChannel.register(selector, SelectionKey.OP_CONNECT);
            }
        }

        private void doWrite(SocketChannel sc) throws IOException {
            byte[] req = "QUERY TIME ORDER".getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
            writeBuffer.put(req);
            writeBuffer.flip();
            sc.write(writeBuffer);
            if (!writeBuffer.hasRemaining()){
                System.out.println("Send order 2 server succeed.");
            }
        }
    }
}
```
NIO 的实现比较复杂，但是 NIO 编程有很多优点：
- 客户端发起的连接操作是异步的，可以通过在多路复用器注册 OP_CONNECT 等待后续结果，不需要同步阻塞
- SocketChannel 的读写操作都是异步的，如果没有可读写的数据它不会同步等待而是直接返回，这样 I/O 线程就可以处理其他的链路而不需要等待当前链路可用
- JDK 的 Selector 在 Linux 系统上使用 epoll 实现使得它没有连接句柄的限制并且不需要轮询所有注册的 Channel，因此在处理千万个客户端连接时系能不会线性下降
### AIO 编程
AIO 引入了异步套接字通道实现了真正的异步阻塞 I/O，它不需要通过多路复用器对注册的通道进行轮询操作即可实现异步读写。
#### 服务器代码示例
```java
public class TimeServer {

    public static void main(String[] args) {
        int port = 8080;
        if (args != null && args.length > 0){
            port = Integer.parseInt(args[0]);
        }
        AsyncTimeServerHandler timeServer = new AsyncTimeServerHandler(port);
        new Thread(timeServer, "aio-asyncTimeServerHandler").start();
    }
}

public class AsyncTimeServerHandler implements Runnable {

    private int port;

    CountDownLatch latch;

    AsynchronousServerSocketChannel asynchronousServerSocketChannel;

    public AsyncTimeServerHandler(int port){
        this.port = port;
        try{
            asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
            asynchronousServerSocketChannel.bind(new InetSocketAddress(port));
            System.out.println("The time server is start in port: " + port);
        }catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }
    @Override
    public void run() {

        latch = new CountDownLatch(1);
        doAccept();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    public void doAccept(){
        asynchronousServerSocketChannel.accept(this, new AcceptCompletionHandler());
    }
}

public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsyncTimeServerHandler> {

    @Override
    public void completed(AsynchronousSocketChannel result, AsyncTimeServerHandler attachment) {
        attachment.asynchronousServerSocketChannel.accept(attachment, this);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        result.read(buffer, buffer, new ReadCompletionHandler(result));
    }

    @Override
    public void failed(Throwable exc, AsyncTimeServerHandler attachment) {
        exc.printStackTrace();
        attachment.latch.countDown();
    }
}

public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {

    private AsynchronousSocketChannel channel;

    public ReadCompletionHandler(AsynchronousSocketChannel channel){
        if (this.channel == null){
            this.channel = channel;
        }
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment) {

        attachment.flip();
        byte[] body = new byte[attachment.remaining()];
        attachment.get(body);
        try {
            String req = new String(body, "utf-8");
            System.out.println("The time server recieve order: " + req);
            String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(req) ? new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
            doWrite(currentTime);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {

        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doWrite(String currentTime){
        if (currentTime != null && currentTime.trim().length() > 0){
            byte[] bytes = currentTime.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();
            channel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    if (attachment.hasRemaining()){
                        channel.write(attachment, attachment, this);
                    }

                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {

                    try {
                        channel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
```
#### 客户端代码示例
```java
public class TimeClient {

    public static void main(String[] args) {
        int port = 8080;
        if (args != null && args.length > 0){
            port = Integer.parseInt(args[0]);
        }
        new Thread(new AsyncTimeClientHandler("127.0.0.1", port), "aio-asyncTimeClientHandler").start();
    }
}

public class AsyncTimeClientHandler implements CompletionHandler<Void, AsyncTimeClientHandler>, Runnable {

    private String host;
    private int port;

    private AsynchronousSocketChannel client;
    private CountDownLatch latch;

    public AsyncTimeClientHandler(String host, int port){
        this.host = host;
        this.port = port;

        try {
            client = AsynchronousSocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void completed(Void result, AsyncTimeClientHandler attachment) {

        byte[] req = "QUERY TIME ORDER".getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
        writeBuffer.put(req);
        writeBuffer.flip();
        client.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                if (attachment.hasRemaining()){
                    client.write(attachment, attachment, this);
                }else {
                    ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                    client.read(readBuffer, readBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                        @Override
                        public void completed(Integer result, ByteBuffer attachment) {
                            attachment.flip();
                            byte[] bytes = new byte[attachment.remaining()];
                            attachment.get(bytes);
                            String body;
                            try {
                                body = new String(bytes, "utf-8");
                                System.out.println("Now is: " + body);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }finally {
                                latch.countDown();
                            }
                        }

                        @Override
                        public void failed(Throwable exc, ByteBuffer attachment) {

                            try {
                                client.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }finally {
                                latch.countDown();
                            }
                        }
                    });
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {

                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    latch.countDown();
                }
            }
        });
    }

    @Override
    public void failed(Throwable exc, AsyncTimeClientHandler attachment) {

        exc.printStackTrace();
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            latch.countDown();
        }
    }

    @Override
    public void run() {
        latch = new CountDownLatch(1);
        client.connect(new InetSocketAddress(host, port), this, this);
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```
## Netty 入门
### Netty 服务端开发
```java
public class TimeServer{
    public void bind(int port) throw Exception{
        // 用于接受客户端的连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 进行 SocketChannel 的网络读写
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootStrap b = new ServerBootStrap();
            b.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG, 1024)
            .chilidHandler(new ChildChannelHandler());
            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        }finally{
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel>{
        protected void initChannel(SocketChannel channel) throws Exception{
            chennl.pipeline().addLast(new TimeServerHandler());
        }
    }

    public static void main(String[] args) throws Exception{
        int port = 8080;
        new TimeServer().bind(port);
    }
}

public class TimeServerHandler extends ChannelHandlerAdapter{
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, "UTF-8");

        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.write(resp);
    }

    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception{
        ctx.flush();
    }

    public void exceptionCaught(ChannelHandlerContext ctx){
        ctx.close();
    }
}
```
### Netty 客户端开发
```java
public class TimeClient {

    public void connect(int port, String host) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new StreamBasedClientHandler());
                }
            });
            // 发起异步连接
            ChannelFuture f = b.connect(host, port).sync();
            f.channel().closeFuture().sync();

        }finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int port = 8080;
        if (args != null && args.length > 0){
            port = Integer.parseInt(args[0]);
        }

        new TimeClient().connect(port, "127.0.0.1");
    }
}

public class TimeClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(TimeClientHandler.class);

    private final ByteBuf firstMessage;

    public TimeClientHandler(){
        byte[] req = "QUERY TIME ORDER".getBytes();
        firstMessage = Unpooled.buffer(req.length);
        firstMessage.writeBytes(req);
    }

    // 客户端和服务端建立连接之后调用
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(firstMessage);
    }

    // 服务端返回应答消息时调用
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, "utf-8");
        System.out.println("Now is: " + body);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("Unexpected exception from downstream: " + cause.getMessage());
        ctx.close();
    }
}
```
### 粘包/拆包问题
TCP/IP 是流协议，传输的是字节流，也就是说 TCP 底层并不了解上层业务数据的具体含义，它会根据 TCP 缓冲区的实际情况进行包的划分，所以一个完整的包可能会被 TCP 拆分成多个包进行发送，也有可能把多个小的包封装成一个大的数据包发送，这就是 TCP 的粘包和拆包问题。
#### TCP 粘包/拆包发生的原因
产生 TCP 粘包/拆包问题的原因有三个：
- 应用程序 write 的字节大小超出套接字接口发送的缓冲区大小
- 进行 MSS 大小的 TCP 分段
- 以太网帧的 payload 大于 MTU 进行 IP 分片
#### 粘包问题解决策略
由于底层的 TCP 无法理解上层的业务数据，所以在底层是无法保证数据包不被拆分和重组的，这个问题只能通过上层的应用协议栈设计解决。目前业界的主流协议解决方案有：
- 消息定长，如每个报文大小为固定长度 200 字节，如果不够空位补空格
- 在包尾增加回车换行符进行切割
- 将消息分为消息头和消息体，消息头中包含表示消息总长度的字段
- 自定义协议
#### LineBasedFrameDecoder
Netty 提供了基于长度的编码器解决半包问题。
```java
public class TimeServer{
    public void bind(int port) throws Exception{
        // 配置服务端的 NIO 线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EvemtLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootStrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG, 1024)
            .childHandler(new ChildChannelHandler());
            // 绑定端口
            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        }finally{
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel>{
        protected void initChannel(SocketChannel channel) throws Exception{
            channel.pipeline().addLast(new LineBasedFrameDecoder(1024))
        }
    }
}
```
