package example.netty.tutorial.dns;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.dns.DatagramDnsQuery;
import io.netty.handler.codec.dns.DatagramDnsResponse;
import io.netty.handler.codec.dns.DefaultDnsQuestion;
import io.netty.handler.codec.dns.DnsQuery;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsRawRecord;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.handler.codec.dns.DnsSection;
import io.netty.util.NetUtil;

import java.net.InetSocketAddress;

import static example.netty.tutorial.dns.UdpDnsClient.DNS_SERVER_HOST;
import static example.netty.tutorial.dns.UdpDnsClient.DNS_SERVER_PORT;
import static example.netty.tutorial.dns.UdpDnsClient.QUERY_DOMAIN;

public class UdpDnsClientHandler extends SimpleChannelInboundHandler<DatagramDnsResponse> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        InetSocketAddress addr = new InetSocketAddress(DNS_SERVER_HOST, DNS_SERVER_PORT);
        DnsQuery dnsQuery = new DatagramDnsQuery(null, addr, 1)
                .setRecord(DnsSection.QUESTION, new DefaultDnsQuestion(QUERY_DOMAIN, DnsRecordType.A));
        ctx.channel().writeAndFlush(dnsQuery).sync();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramDnsResponse msg) throws Exception {
        handleQueryResp(msg);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private void handleQueryResp(DatagramDnsResponse msg) {
        if (msg.count(DnsSection.QUESTION) > 0) {
            DnsQuestion question = msg.recordAt(DnsSection.QUESTION, 0);
            System.out.printf("name: %s%n", question.name());
        }
        for (int i = 0, count = msg.count(DnsSection.ANSWER); i < count; i++) {
            DnsRecord record = msg.recordAt(DnsSection.ANSWER, i);
            if (record.type() == DnsRecordType.A) {
                //just print the IP after query
                DnsRawRecord raw = (DnsRawRecord) record;
                System.out.println(NetUtil.bytesToIpAddress(ByteBufUtil.getBytes(raw.content())));
            }
        }
    }
}
