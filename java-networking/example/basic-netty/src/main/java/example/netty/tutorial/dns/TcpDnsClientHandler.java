package example.netty.tutorial.dns;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.dns.*;
import io.netty.util.NetUtil;

import java.util.Random;

import static example.netty.tutorial.dns.TcpDnsClient.QUERY_DOMAIN;

public class TcpDnsClientHandler extends SimpleChannelInboundHandler<DefaultDnsResponse> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        int randomID = new Random().nextInt(60000 - 1000) + 1000;
        DnsQuery query = new DefaultDnsQuery(randomID, DnsOpCode.QUERY)
            .setRecord(DnsSection.QUESTION, new DefaultDnsQuestion(QUERY_DOMAIN, DnsRecordType.A));
        ctx.writeAndFlush(query);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DefaultDnsResponse msg) throws Exception {
        handleQueryResponse(msg);
        ctx.flush();
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private void handleQueryResponse(DefaultDnsResponse msg){
        if (msg.count(DnsSection.QUESTION) > 0){
            DnsQuestion question = msg.recordAt(DnsSection.QUESTION, 0);
            System.out.printf("name: %s%n", question.name());
        }
        for (int i = 0, count = msg.count(DnsSection.ANSWER); i < count; i++){
            DnsRecord record = msg.recordAt(DnsSection.ANSWER, i);
            if (record.type() == DnsRecordType.A){
                DnsRawRecord raw = (DnsRawRecord) record;
                System.out.println(NetUtil.bytesToIpAddress(ByteBufUtil.getBytes(raw.content())));
            }
        }
    }
}
