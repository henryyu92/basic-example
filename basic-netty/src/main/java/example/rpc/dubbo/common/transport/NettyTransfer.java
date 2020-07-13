package example.rpc.dubbo.common.transport;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * 使用 Netty 作为传输数据方式
 */
public class NettyTransfer implements Transporter {

    public static void start(String hostname, int port){
        doStart(hostname, port);
    }

    private static void doStart(String hostname, int port){
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        try{

            ServerBootstrap b = new ServerBootstrap();

            b.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new StringDecoder())
                                    .addLast(new StringEncoder())
                                    .addLast(null);
                        }
                    });
            ChannelFuture future = b.bind(hostname, port).sync();

            future.channel().closeFuture();


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    @Override
    public void export() {

    }

    @Override
    public void refer() {

    }
}
