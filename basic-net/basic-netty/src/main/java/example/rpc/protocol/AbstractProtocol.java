package example.rpc.protocol;

public abstract class AbstractProtocol<T> implements Protocol<T> {

    @Override
    public void export() {
        check();
        doExport();
    }

    @Override
    public T refer() {
        check();
        return doRefer();
    }

    /**
     * 检查是否满足协议
     */
    protected abstract void check();

    protected abstract void doExport();

    protected abstract T doRefer();
}
