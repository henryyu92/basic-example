package example.netty.tutorial.echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

public class EchoServer {

    private static final boolean SSL = Boolean.parseBoolean(System.getProperty("ssl", "false"));
    private static final int PORT = Integer.parseInt(System.getProperty("port", "8009"));

    ServerBootstrap b;
    NioEventLoopGroup bossGroup;
    NioEventLoopGroup workerGroup;

    public EchoServer(){
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        b = new ServerBootstrap();

        b.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG, 100)
            .handler(new LoggingHandler(LogLevel.INFO))
            .childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    SslContext sslCtx = sslContext();
                    if (sslCtx != null){
                        p.addLast(sslCtx.newHandler(ch.alloc()));
                    }
                    p.addLast(new LoggingHandler(LogLevel.INFO))
                        .addLast(new EchoServerHandler());
                }
            });
    }

    public void start() throws Exception{
        try{
            // Bind port and start server
            ChannelFuture f = b.bind(PORT).sync();

            // Wait until the socket channel closed
            f.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private SslContext sslContext() throws Exception{
        if (SSL){
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            return SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        EchoServer server = new EchoServer();
        server.start();
    }
}
