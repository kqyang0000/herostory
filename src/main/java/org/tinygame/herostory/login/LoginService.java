package org.tinygame.herostory.login;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.MySqlSessionFactory;
import org.tinygame.herostory.async.AsyncOperationProcessor;
import org.tinygame.herostory.async.IAsyncOperation;
import org.tinygame.herostory.login.db.IUserDao;
import org.tinygame.herostory.login.db.UserEntity;

import java.util.function.Function;

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
    public void userLogin(String userName, String password, Function<UserEntity, Void> callback) {
        if (userName == null || password == null) {
            return;
        }

        AsyncGetUserEntity asyncOp = new AsyncGetUserEntity(userName, password) {

            @Override
            public int getBindId() {
                return userName.charAt(userName.length() - 1);
            }

            @Override
            public void doFinish() {
                if (null != callback) {
                    callback.apply(this.getUserEntity());
                }
            }
        };
        AsyncOperationProcessor.getInstance().process(asyncOp);
    }


    private class AsyncGetUserEntity implements IAsyncOperation {

        private final String _userName;
        private final String _password;

        /**
         * 用户实体
         */
        private UserEntity _userEntity;

        public UserEntity getUserEntity() {
            return _userEntity;
        }

        public AsyncGetUserEntity(String userName, String password) {
            this._userName = userName;
            this._password = password;
        }

        @Override
        public void doAsync() {
            try (SqlSession mySqlSession = MySqlSessionFactory.openSession()) {
                IUserDao dao = mySqlSession.getMapper(IUserDao.class);

                UserEntity userEntity = dao.getUserByName(_userName);

                LOGGER.info("当前线程 = {}", Thread.currentThread().getName());

                if (userEntity != null) {

                    if (!_password.equals(userEntity.getPassword())) {
                        throw new RuntimeException("用户名密码错误");
                    }
                } else {
                    userEntity = new UserEntity();
                    userEntity.setUserName(_userName);
                    userEntity.setPassword(_password);
                    userEntity.setHeroAvatar("Hero_Shaman");
                    dao.insertInfo(userEntity);
                }

                this._userEntity = userEntity;
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }
}


