package com.mybatis.my_mybatis.sqlSession;

import com.mybatis.my_mybatis.configuration.Configuration;
import com.mybatis.my_mybatis.executor.BaseExecutor;
import com.mybatis.my_mybatis.mapperProxy.MapperProxy;

import java.lang.reflect.Proxy;

public class SQLSession {

    private BaseExecutor executor = new BaseExecutor();

    private Configuration configuration = new Configuration();

    public <T> T selectOne(String statement, Object parameter) {
        return executor.query(statement, parameter);
    }

    @SuppressWarnings("unchecked")
    public <T> T getMapper(Class<T> clas) {
        //动态代理调用
        return (T) Proxy.newProxyInstance(clas.getClassLoader(), new Class[]{clas},
                new MapperProxy(this, configuration));
    }
}
