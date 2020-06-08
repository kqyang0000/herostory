package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.cmdhandler.CmdHandlerFactory;
import org.tinygame.herostory.cmdhandler.ICmdHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class MainMsgProcessor {
    static private final Logger LOGGER = LoggerFactory.getLogger(MainMsgProcessor.class);

    /**
     * 单例对象
     */
    static private final MainMsgProcessor _instance = new MainMsgProcessor();

    private MainMsgProcessor() {
    }

    /**
     * 获取单例对象
     *
     * @return
     */
    static public MainMsgProcessor getInstance() {
        return _instance;
    }

    /**
     * 创建一个单线程的线程池
     */
    private final ExecutorService _es = Executors.newSingleThreadExecutor((newRunnable) -> {
        Thread newThread = new Thread(newRunnable);
        newThread.setName("MainMsgProcessor");
        return newThread;
    });


    public void process(ChannelHandlerContext ctx, Object msg) {
        if (msg == null) {
            return;
        }

        LOGGER.info(
                "收到客户端消息，msgType = {}, msg = {}",
                msg.getClass().getSimpleName(),
                msg
        );

        _es.submit(() -> {
            try {
                ICmdHandler<? extends GeneratedMessageV3> cmdHandler = CmdHandlerFactory.create(msg.getClass());
                if (cmdHandler != null) {
                    cmdHandler.handle(ctx, cast(msg));
                }
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        });

    }

    static private <TCmd extends GeneratedMessageV3> TCmd cast(Object msg) {
        if (msg == null) {
            return null;
        } else {
            return (TCmd) msg;
        }
    }
}
