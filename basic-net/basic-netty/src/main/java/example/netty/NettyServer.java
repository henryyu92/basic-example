package example.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {
    public static void main(String[] args) throws InterruptedException {

        /**
         * boss Group 只处理连接请求
         * 无限循环
         */
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        /**
         * worker Group 处理客户端事件以及业务处理
         * 无限循环
         */
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();


        ServerBootstrap bootstrap = new ServerBootstrap();
        try{

            bootstrap.group(bossGroup, workerGroup) // 设置线程组
                    .channel(NioServerSocketChannel.class)// NioServerSocketChannel 作为 Server Channel 实现
                    .option(ChannelOption.SO_BACKLOG, 128)// 设置线程队列得到的连接数
                    .childOption(ChannelOption.SO_KEEPALIVE, true) // 设置保持活动连接状态
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 设置事件处理器
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

        System.out.println("服务器准备好了。。。。。。");
        // 绑定端口
        ChannelFuture future = bootstrap.bind(6668).sync();

        // 对关闭通道进行监听
        future.channel().closeFuture().sync();
    }
}
