package org.tinygame.herostory.login;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.MySqlSessionFactory;
import org.tinygame.herostory.login.db.IUserDao;
import org.tinygame.herostory.login.db.UserEntity;

/**
 * 登录服务
 */
public final class LoginService {
    static private final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    static private final LoginService _instance = new LoginService();

    private LoginService() {
    }

    /**
     * 获取实例
     *
     * @return
     */
    static public LoginService getInstance() {
        return _instance;
    }

    /**
     * 用户登录
     *
     * @param userName
     * @param password
     * @return
     */
    public UserEntity userLogin(String userName, String password) {
        if (userName == null || password == null) {
            return null;
        }

        try (SqlSession mySqlSession = MySqlSessionFactory.openSession()) {
            IUserDao dao = mySqlSession.getMapper(IUserDao.class);

            UserEntity userEntity = dao.getUserByName(userName);

            if (userEntity != null) {
                if (!password.equals(userEntity.getPassword())) {
                    throw new RuntimeException("用户名密码错误");
                }
            } else {
                userEntity = new UserEntity();
                userEntity.setUserName(userName);
                userEntity.setPassword(password);
                userEntity.setHeroAvatar("Hero_Shaman");
                dao.insertInfo(userEntity);
            }

            return userEntity;
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return null;
        }
    }
}
