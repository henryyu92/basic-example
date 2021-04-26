package example.netty.tutorial.discard;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.internal.SystemPropertyUtil;

public class DiscardServer {

    // -Dssl=true
    private static final boolean SSL = SystemPropertyUtil.getBoolean("ssl", false);
    private static final int PORT = SystemPropertyUtil.getInt("port", 8009);

    private final ServerBootstrap b;
    private final NioEventLoopGroup bossGroup;
    private final NioEventLoopGroup workerGroup;

    private SslContext sslContext() throws Exception {
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            return SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        }
        return null;
    }

    public DiscardServer() {

        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .handler(new LoggingHandler(LogLevel.INFO))
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    SslContext sslContext = sslContext();
                    if (sslContext != null) {
                        p.addLast(sslContext.newHandler(ch.alloc()));
                    }
                    p.addLast(new DiscardServerHandler());
                }
            });
    }

    public void start() throws Exception {
        try {
            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(PORT).sync();

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {

        DiscardServer server = new DiscardServer();
        server.start();
    }

}
