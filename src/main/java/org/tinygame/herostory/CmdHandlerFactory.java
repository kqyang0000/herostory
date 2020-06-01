package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.cmdhandler.ICmdHandler;
import org.tinygame.herostory.util.PackageUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 命令处理器工厂类
 */
public final class CmdHandlerFactory {
    static private final Logger LOGGER = LoggerFactory.getLogger(CmdHandlerFactory.class);

    static private final Map<Class<?>, ICmdHandler<? extends GeneratedMessageV3>> _handlerMap = new HashMap<>();

    private CmdHandlerFactory() {
    }

    static public void init() {
        LOGGER.info("==== 建立处理器与命令关联 ====");

        //获取包名称
        final String packageName = CmdHandlerFactory.class.getPackage().getName();

        //获取IcmdHandler 多有的实现类
        Set<Class<?>> clazzSet = PackageUtil.listSubClazz(packageName, true, ICmdHandler.class);


        for (Class<?> handleClazz : clazzSet) {
            if (null == handleClazz || 0 != (handleClazz.getModifiers() & Modifier.ABSTRACT)) {
                continue;
            }

            //获取方法数组
            Method[] methodArray = handleClazz.getDeclaredMethods();

            Class<?> cmdClazz = null;

            for (Method currMethod : methodArray) {
                if (null == currMethod || !currMethod.getName().equals("handle")) {
                    continue;
                }

                //获取函数参数类型数组
                Class<?>[] paramTypeArray = currMethod.getParameterTypes();

                if (paramTypeArray.length < 2 || paramTypeArray[1] == GeneratedMessageV3.class ||
                        !GeneratedMessageV3.class.isAssignableFrom(paramTypeArray[1])) {
                    continue;
                }

                cmdClazz = paramTypeArray[1];
                break;
            }
            if (null == cmdClazz) {
                continue;
            }

            try {
                //创建实例
                ICmdHandler<?> newHandler = (ICmdHandler<?>) handleClazz.newInstance();

                LOGGER.info("{} <==> {}", cmdClazz.getName(), handleClazz.getName());
                _handlerMap.putIfAbsent(cmdClazz, newHandler);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
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
