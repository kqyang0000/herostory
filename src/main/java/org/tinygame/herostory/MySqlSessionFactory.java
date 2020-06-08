package org.tinygame.herostory;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mysql 会话工厂
 */
public final class MySqlSessionFactory {
    static private final Logger LOGGER = LoggerFactory.getLogger(MySqlSessionFactory.class);

    /**
     * Mybatis Sql 会话工厂
     */
    static private SqlSessionFactory _sqlSessionFactory;

    private MySqlSessionFactory() {
    }

    /**
     * 初始化
     */
    static public void init() {
        try {
            _sqlSessionFactory = (new SqlSessionFactoryBuilder()).build(
                    Resources.getResourceAsStream("MybatisConfig.xml")
            );

            //测试数据库连接
            SqlSession tempSession = openSession();

            tempSession.getConnection().createStatement().execute("SELECT -1");

            tempSession.close();

            LOGGER.info("Mysql 数据库连接测试成功");
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * 创建Mysql 会话
     *
     * @return
     */
    static public SqlSession openSession() {
        if (null == _sqlSessionFactory) {
            throw new RuntimeException("_sqlSessionFactory 尚未初始化");
        }

        return _sqlSessionFactory.openSession(true);
    }


}
