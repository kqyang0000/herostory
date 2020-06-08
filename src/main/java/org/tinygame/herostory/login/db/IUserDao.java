package org.tinygame.herostory.login.db;

public interface IUserDao {
    /**
     * 根据用户名称获取实体
     *
     * @param userName
     * @return
     */
    UserEntity getUserByName(String userName);

    /**
     * 添加用户实体
     *
     * @param newEntity
     */
    void insertInfo(UserEntity newEntity);
}
