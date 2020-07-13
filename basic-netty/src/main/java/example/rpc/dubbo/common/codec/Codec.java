package example.rpc.dubbo.common.codec;

/**
 * 定义网络数据编解码(序列化)
 */
public interface Codec<T> {

    /**
     * 解码
     * @param msg
     * @return
     */
    T decode(byte[] msg);

    /**
     * 编码
     * @param msg
     * @return
     */
    byte[] encode(T msg);
}
