package example.rpc.dubbo.common.protocol;

/**
 * 数据传输协议
 */
public interface Protocol<T> {

    /**
     * 数据传输协议名
     * @return
     */
    String getName();

    /**
     * 协议端口
     * @return
     */
    Integer getPort();

    /**
     * 暴露服务
     */
    void export();

    /**
     * 引用服务
     */
    void refer();

}
