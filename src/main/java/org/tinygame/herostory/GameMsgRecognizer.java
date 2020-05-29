package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import org.tinygame.herostory.msg.GameMsgProtocol;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息识别器
 */
public final class GameMsgRecognizer {

    /**
     * 消息编号 -》 消息对象字典
     */
    static private final Map<Integer, GeneratedMessageV3> _msgCodeAndMsgObjMap = new HashMap<>();
    /**
     * 消息类 -》消息编号字典
     */
    static private final Map<Class<?>, Integer> _clazzAndMsgCodeMap = new HashMap<>();

    /**
     * 初始化
     */
    static public void init() {
        _msgCodeAndMsgObjMap.put(GameMsgProtocol.MsgCode.USER_ENTRY_CMD_VALUE, GameMsgProtocol.UserEntryCmd.getDefaultInstance());
        _msgCodeAndMsgObjMap.put(GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_CMD_VALUE, GameMsgProtocol.WhoElseIsHereCmd.getDefaultInstance());
        _msgCodeAndMsgObjMap.put(GameMsgProtocol.MsgCode.USER_MOVE_TO_CMD_VALUE, GameMsgProtocol.UserMoveToCmd.getDefaultInstance());

        _clazzAndMsgCodeMap.put(GameMsgProtocol.UserEntryResult.class, GameMsgProtocol.MsgCode.USER_ENTRY_RESULT_VALUE);
        _clazzAndMsgCodeMap.put(GameMsgProtocol.WhoElseIsHereResult.class, GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_RESULT_VALUE);
        _clazzAndMsgCodeMap.put(GameMsgProtocol.UserMoveToResult.class, GameMsgProtocol.MsgCode.USER_MOVE_TO_RESULT_VALUE);
        _clazzAndMsgCodeMap.put(GameMsgProtocol.UserQuitResult.class, GameMsgProtocol.MsgCode.USER_QUIT_RESULT_VALUE);

    }

    private GameMsgRecognizer() {
    }

    static public Message.Builder getBuilderByMsgCode(int msgCode) {
        if (msgCode < 0) {
            return null;
        }

        GeneratedMessageV3 defaultMsg = _msgCodeAndMsgObjMap.get(msgCode);

        if (defaultMsg == null) {
            return null;
        } else {
            return defaultMsg.newBuilderForType();
        }
    }

    static public int getMsgCodeByClass(Class<?> msgClazz) {
        if (msgClazz == null) {
            return -1;
        }
        Integer msgCode = _clazzAndMsgCodeMap.get(msgClazz);
        if (msgCode == null) {
            return -1;
        } else {
            return msgCode.intValue();
        }
    }
}
