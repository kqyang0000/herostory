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
     * 构造器私有化
     */
    private AsyncOperationProcessor() {
    }

    /**
     * 创建一个单线程的线程池
     */
    private final ExecutorService _es = Executors.newSingleThreadExecutor((newRunnable) -> {
        Thread newThread = new Thread(newRunnable);
        newThread.setName("AsyncOperationProcessor");
        return newThread;
    });

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

        _es.submit(() -> {
            //执行异步操作
            op.doAsync();
            //回到主线程执行完成操作
            MainMsgProcessor.getInstance().process(op::doFinish);
        });
    }

}
