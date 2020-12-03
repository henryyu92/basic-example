package example.rpc.protocol;

public abstract class AbstractProtocol<T> implements Protocol<T> {

    @Override
    public void export() {
        check();
        doExport();
    }

    @Override
    public void refer() {
        check();
        doRefer();
    }

    /**
     * 检查是否满足协议
     */
    protected abstract void check();

    protected abstract void doExport();

    protected abstract void doRefer();
}
