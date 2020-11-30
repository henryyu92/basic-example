package example.netty.tutorial.discard;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.internal.SystemPropertyUtil;

import javax.net.ssl.SSLException;

public class DiscardClient {

    private static final boolean SSL = SystemPropertyUtil.getBoolean("ssl", false);
    private static final int PORT = SystemPropertyUtil.getInt("port", 8009);
    private static final String HOST = SystemPropertyUtil.get("host", "127.0.0.1");
    public static final int SIZE = SystemPropertyUtil.getInt("size", 256);

    private Bootstrap b;
    private NioEventLoopGroup group;

    private SslContext sslContext() throws SSLException {
        if (SSL){
            return SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
        }
        return null;
    }

    public DiscardClient(){
        b = new Bootstrap();
        group = new NioEventLoopGroup();
        b.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();
                SslContext sslContext = sslContext();
                if (sslContext != null){
                    p.addLast(sslContext.newHandler(ch.alloc(), HOST, PORT));
                }
                p.addLast(new DiscardClientHandler());
            }
        });
    }

    public void connect() throws Exception {
        try{
            // Connect to server
            ChannelFuture f = b.connect(HOST, PORT).sync();
            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully();
        }

    }

    public static void main(String[] args) throws Exception {
        DiscardClient client = new DiscardClient();
        client.connect();
    }
}
