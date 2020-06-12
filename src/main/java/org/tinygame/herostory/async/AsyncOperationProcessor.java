package org.tinygame.herostory.async;

import org.tinygame.herostory.MainMsgProcessor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步操作处理器
 */
public final class AsyncOperationProcessor {
    /**
     * 单例对象
     */
    static private final AsyncOperationProcessor _instance = new AsyncOperationProcessor();

    /**
     * 创建单线程数组
     */
    private final ExecutorService[] _esArray = new ExecutorService[8];

    /**
     * 构造器私有化
     */
    private AsyncOperationProcessor() {
        for (int i = 0; i < _esArray.length; i++) {
            final String threadName = "AsyncOperationProcessor[ " + i + " ]";
            _esArray[i] = Executors.newSingleThreadExecutor((r) -> {
                Thread t = new Thread(r);
                t.setName(threadName);
                return t;
            });

        }
    }

    /**
     * 获取单例对象
     *
     * @return
     */
    static public AsyncOperationProcessor getInstance() {
        return _instance;
    }

    /**
     * 执行异步操作
     *
     * @param op
     */
    public void process(IAsyncOperation op) {
        if (op == null) {
            return;
        }

        int bindId = Math.abs(op.getBindId());
        int esIndex = bindId % _esArray.length;

        _esArray[esIndex].submit(() -> {
            //执行异步操作
            op.doAsync();
            //回到主线程执行完成操作
            MainMsgProcessor.getInstance().process(op::doFinish);
        });
    }

}
