package example.rpc.dubbo.common.codec;

import io.netty.channel.ChannelDuplexHandler;

/**
 * Netty 编解码
 * @param <T>
 */
public class NettyCodecAdaptor<T> extends ChannelDuplexHandler {

    private Codec<T> codec;

    public NettyCodecAdaptor(Codec<T> codec){
        this.codec = codec;
    }

}
