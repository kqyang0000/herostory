package org.tinygame.herostory;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * 广播员
 */
public final class Broadcaster {

    /**
     * 信道组，这里一定要用static，
     * 否则无法实现群发
     */
    static private final ChannelGroup _channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private Broadcaster() {
    }

    /**
     * 添加信道
     *
     * @param ch
     */
    static public void addChannel(Channel ch) {
        if (ch != null) {
            _channelGroup.add(ch);
        }
    }

    /**
     * 移除信道
     *
     * @param ch
     */
    static public void removeChannel(Channel ch) {
        if (ch != null) {
            _channelGroup.remove(ch);
        }
    }

    /**
     * 广播消息
     *
     * @param msg
     */
    static public void broadcast(Object msg) {
        if (msg != null) {
            _channelGroup.writeAndFlush(msg);
        }
    }
}
