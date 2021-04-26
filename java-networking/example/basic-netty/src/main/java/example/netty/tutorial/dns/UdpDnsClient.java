package example.netty.tutorial.dns;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.dns.DatagramDnsQueryDecoder;
import io.netty.handler.codec.dns.DatagramDnsQueryEncoder;
import io.netty.handler.codec.dns.DatagramDnsResponseDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.concurrent.TimeUnit;

public class UdpDnsClient {

    public static final String QUERY_DOMAIN = "www.example.com";
    public static final int DNS_SERVER_PORT = 53;
    public static final String DNS_SERVER_HOST = "8.8.8.8";

    private final Bootstrap b = new Bootstrap();
    private final NioEventLoopGroup group;

    public UdpDnsClient(){
        group = new NioEventLoopGroup();

        b.group(group).channel(NioDatagramChannel.class).handler(new ChannelInitializer<DatagramChannel>() {
            @Override
            protected void initChannel(DatagramChannel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();
                p.addLast(new LoggingHandler(LogLevel.INFO))
                    .addLast(new DatagramDnsQueryEncoder())
                    .addLast(new DatagramDnsResponseDecoder())
                    .addLast(new UdpDnsClientHandler());
            }
        });
    }

    public void connect() throws Exception {
        try{
            ChannelFuture f = b.connect(DNS_SERVER_HOST, DNS_SERVER_PORT).sync();

            boolean success = f.channel().closeFuture().await(10, TimeUnit.SECONDS);
            if (!success){
                System.err.println("dns query timeout!");
                f.channel().close().sync();
            }
        }finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        UdpDnsClient client = new UdpDnsClient();
        client.connect();
    }
}
