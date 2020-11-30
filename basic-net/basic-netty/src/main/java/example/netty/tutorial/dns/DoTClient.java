package example.netty.tutorial.dns;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.dns.TcpDnsQueryEncoder;
import io.netty.handler.codec.dns.TcpDnsResponseDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import java.util.concurrent.TimeUnit;

// DNS over TLS
public class DoTClient {

    public static final String QUERY_DOMAIN = "www.example.com";
    private static final int DNS_SERVER_PORT = 853;
    // 114.114.114.114 or 8.8.8.8
    private static final String DNS_SERVER_HOST = "8.8.8.8";

    private Bootstrap b = new Bootstrap();
    private NioEventLoopGroup group;

    public DoTClient() {
        group = new NioEventLoopGroup();
        b.group(group)
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    SslContext sslCtx = sslContext();
                    p.addLast(sslCtx.newHandler(ch.alloc(), DNS_SERVER_HOST, DNS_SERVER_PORT))
                        .addLast(new LoggingHandler(LogLevel.INFO))
                        .addLast(new TcpDnsQueryEncoder())
                        .addLast(new TcpDnsResponseDecoder())
                        .addLast(new DoTClientHandler());
                }
            });
    }

    public void connect() throws Exception {
        try {
            ChannelFuture f = b.connect(DNS_SERVER_HOST, DNS_SERVER_PORT).sync();
            // Wait 10s
            boolean success = f.channel().closeFuture().await(10, TimeUnit.SECONDS);
            // if channel is open, force close it
            if (!success) {
                System.out.println("dns query timeout!");
                // close channel
                f.channel().close().sync();
            }
        } finally {
            group.shutdownGracefully();
        }
    }

    private SslContext sslContext() throws Exception {
        return SslContextBuilder.forClient().protocols("TLSv1.3", "TLSv1.2").build();
    }

    public static void main(String[] args) throws Exception {
        DoTClient client = new DoTClient();
        client.connect();
    }
}
