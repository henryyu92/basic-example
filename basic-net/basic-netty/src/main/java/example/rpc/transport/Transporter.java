package example.rpc.transport;

/**
 * 网络传输方式
 */
public interface Transporter {

    /**
     * 暴露服务
     */
    public void export();

    /**
     * 引用服务
     */
    void refer();
}
