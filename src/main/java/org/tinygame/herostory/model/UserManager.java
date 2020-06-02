package org.tinygame.herostory.model;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户管理器
 */
public final class UserManager {
    static private final Map<Integer, User> _userMap = new ConcurrentHashMap<>();

    private UserManager() {
    }

    /**
     * 添加用户
     *
     * @param u
     */
    static public void addUser(User u) {
        if (u == null) {
            return;
        }
        _userMap.putIfAbsent(u.getUserId(), u);
    }

    /**
     * 移除用户
     *
     * @param userId
     */
    static public void removeByUserId(int userId) {
        _userMap.remove(userId);
    }

    /**
     * 获取用户集合
     *
     * @return
     */
    static public Collection<User> listUser() {
        return _userMap.values();
    }

    /**
     * 通过userId获取用户
     *
     * @param userId
     * @return
     */
    static public User getByUserId(int userId) {
        return _userMap.get(userId);
    }

}
