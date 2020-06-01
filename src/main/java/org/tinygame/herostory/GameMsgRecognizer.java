package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.msg.GameMsgProtocol;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息识别器
 */
public final class GameMsgRecognizer {
    static private final Logger LOGGER = LoggerFactory.getLogger(GameMsgRecognizer.class);

    /**
     * 消息编号 -》 消息对象字典
     */
    static private final Map<Integer, GeneratedMessageV3> _msgCodeAndMsgObjMap = new HashMap<>();
    /**
     * 消息类 -》消息编号字典
     */
    static private final Map<Class<?>, Integer> _msgClazzAndMsgCodeMap = new HashMap<>();

    /**
     * 初始化
     */
    static public void init() {
        LOGGER.info("==== 完成消息类与消息编号的映射 ====");

        //获取内部类
        Class<?>[] innerClazzArray = GameMsgProtocol.class.getDeclaredClasses();

        for (Class<?> innerClazz : innerClazzArray) {
            if (innerClazz == null || !GeneratedMessageV3.class.isAssignableFrom(innerClazz)) {
                continue;
            }

            //获取名称并小写
            String clazzName = innerClazz.getSimpleName();
            clazzName = clazzName.toLowerCase();

            for (GameMsgProtocol.MsgCode msgCode : GameMsgProtocol.MsgCode.values()) {
                if (msgCode == null) {
                    continue;
                }

                //获取消息编码
                String strMsgCode = msgCode.name();
                strMsgCode = strMsgCode.replaceAll("_", "");
                strMsgCode = strMsgCode.toLowerCase();

                if (!strMsgCode.startsWith(clazzName)) {
                    continue;
                }

                try {
                    // 相当于调用userEntryCmd.getDefaultInstance();
                    Object returnObj = innerClazz.getDeclaredMethod("getDefaultInstance").invoke(innerClazz);

                    LOGGER.info("{} <==> {}", innerClazz.getName(), msgCode.getNumber());

                    _msgCodeAndMsgObjMap.putIfAbsent(msgCode.getNumber(), (GeneratedMessageV3) returnObj);

                    _msgClazzAndMsgCodeMap.putIfAbsent(innerClazz, msgCode.getNumber());
                } catch (Exception ex) {
                    LOGGER.error(ex.getMessage(), ex);
                }

            }
        }
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
        Integer msgCode = _msgClazzAndMsgCodeMap.get(msgClazz);
        if (msgCode == null) {
            return -1;
        } else {
            return msgCode.intValue();
        }
    }
}
