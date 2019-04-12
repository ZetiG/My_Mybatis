package com.mybatis.my_mybatis.executor;

import com.mybatis.my_mybatis.configuration.Configuration;
import com.mybatis.web.entity.UserEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 执行器的具体实现，封装了jdbc
 */
public class BaseExecutor implements Executor {

    private Configuration configuration = new Configuration();

    /**
     * 获取连接，读取配置文件
     *
     * @return
     */
    private Connection getConnection() {
        try {
            Connection connection = configuration.build("mybatis-config.xml");
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 具体的查询方法的实现
     *
     * @param sql
     * @param parameter
     * @param <T>
     * @return
     */
    @Override
    public <T> T query(String sql, Object parameter) {
        Connection connection = getConnection();
        ResultSet result = null;
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(sql);

            //设置参数
            statement.setString(1, parameter.toString());
            result = statement.executeQuery();
            UserEntity u = new UserEntity();

            //遍历结果集,将结果赋值给实体类，并返回实体类
            while (result.next()) {
                u.setId(result.getInt(1));
                u.setName(result.getString(2));
            }
            return (T) u;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //关闭连接
            try {
                if (result != null) {
                    result.close();
                }
                if (statement != null) {

                    statement.close();

                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


}
