package example.netty.tutorial.echo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

public class EchoClient {

    private static final boolean SSL = Boolean.parseBoolean(System.getProperty("ssl", "false"));
    private static final String HOST = System.getProperty("host", "127.0.0.1");
    private static final int PORT = Integer.parseInt(System.getProperty("port", "8009"));
    private static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));

    private Bootstrap b;
    private NioEventLoopGroup group;

    public EchoClient(){
        b = new Bootstrap();
        group = new NioEventLoopGroup();

        b.group(group).channel(NioSocketChannel.class)
            .option(ChannelOption.TCP_NODELAY, true)
            .handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();
                SslContext sslCtx = sslContext();
                if (sslCtx != null){
                    p.addLast(sslCtx.newHandler(ch.alloc(), HOST, PORT));
                }
                p.addLast(new LoggingHandler(LogLevel.INFO))
                    .addLast(new EchoClientHandler(SIZE));
            }
        });
    }

    public void connect() throws Exception {
        try{
            ChannelFuture f = b.connect(HOST, PORT).sync();

            f.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully();
        }
    }

    private SslContext sslContext() throws Exception{
        if (SSL){
            return SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        EchoClient client = new EchoClient();
        client.connect();
    }
}
