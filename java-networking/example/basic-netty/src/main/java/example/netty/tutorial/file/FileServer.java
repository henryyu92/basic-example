package example.netty.tutorial.file;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.SystemPropertyUtil;

public class FileServer {

    private static final boolean SSL = SystemPropertyUtil.getBoolean("ssl", false);
    private static final int PORT = SystemPropertyUtil.getInt("port", SSL ? 8443 : 8848);

    private final ServerBootstrap b = new ServerBootstrap();
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;

    public FileServer(){

        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        b.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG, 100)
            .handler(new LoggingHandler(LogLevel.INFO))
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    SslContext sslCtx = sslContext();
                    if (sslCtx != null){
                        p.addLast(sslCtx.newHandler(ch.alloc()));
                        p.addLast(
                            new StringEncoder(CharsetUtil.UTF_8),
                            new LineBasedFrameDecoder(8192),
                            new StringDecoder(CharsetUtil.UTF_8),
                            new ChunkedWriteHandler(),
                            new FileServerHandler());
                    }
                }
            });

    }

    private void start() throws Exception {

        try{
            Channel channel = b.bind(PORT).sync().channel();
            channel.closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private SslContext sslContext() throws Exception {
        if (SSL){
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            return SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        }
        return null;
    }
}
