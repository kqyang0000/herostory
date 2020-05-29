package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import org.tinygame.herostory.cmdhandler.ICmdHandler;
import org.tinygame.herostory.cmdhandler.UserEntryCmdHandler;
import org.tinygame.herostory.cmdhandler.UserMoveToCmdHandler;
import org.tinygame.herostory.cmdhandler.WhoElseIsHereCmdHandler;
import org.tinygame.herostory.msg.GameMsgProtocol;

import java.util.HashMap;
import java.util.Map;

/**
 * 命令处理器工厂类
 */
public final class CmdHandlerFactory {

    static private Map<Class<?>, ICmdHandler<? extends GeneratedMessageV3>> _handlerMap = new HashMap<>();

    private CmdHandlerFactory() {
    }

    static public void init() {
        _handlerMap.put(GameMsgProtocol.UserEntryCmd.class, new UserEntryCmdHandler());
        _handlerMap.put(GameMsgProtocol.WhoElseIsHereCmd.class, new WhoElseIsHereCmdHandler());
        _handlerMap.put(GameMsgProtocol.UserMoveToCmd.class, new UserMoveToCmdHandler());
    }

    /**
     * 创建命令处理器
     *
     * @param msgClass
     * @return
     */
    static public ICmdHandler<? extends GeneratedMessageV3> create(Class<?> msgClass) {
        if (msgClass == null) {
            return null;
        }
        return _handlerMap.get(msgClass);
    }
}
