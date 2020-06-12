package org.tinygame.herostory.async;

/**
 * 异步操作接口
 */
public interface IAsyncOperation {

    /**
     * 获取绑定 ID
     */
    default int getBindId() {
        return 0;
    }

    /**
     * 执行一步操作
     */
    void doAsync();

    /**
     * 执行完成逻辑
     */
    default void doFinish() {

    }
}
