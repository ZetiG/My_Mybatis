package com.mybatis.my_mybatis.mapperProxy;

import com.mybatis.my_mybatis.configuration.Configuration;
import com.mybatis.my_mybatis.configuration.Function;
import com.mybatis.my_mybatis.configuration.MapperBean;
import com.mybatis.my_mybatis.sqlSession.SQLSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * MapperProxy代理类完成xml方法和真实方法对应，执行查询
 */
public class MapperProxy implements InvocationHandler {

    private SQLSession sqlSession;

    private Configuration configuration;

    public MapperProxy(SQLSession sqlSession, Configuration configuration) {
        this.sqlSession = sqlSession;
        this.configuration = configuration;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        //读取mapper.xml文件
        MapperBean mapperBean = configuration.readMapper("mapper/UserMapper.xml");
        //是否是xml文件对应的接口
        if (!method.getDeclaringClass().getName().equals(mapperBean.getInterfaceName())) {
            return null;
        }

        //遍历接口下所有的方法
        List<Function> list = mapperBean.getList();
        if (null != list && list.size() != 0) {
            for (Function function : list) {
                //判断ID和接口的方法名是否相同
                if (method.getName().equals(function.getFuncName())) {
                    return sqlSession.selectOne(function.getSql(), String.valueOf(args[0]));
                }
            }
        }
        return null;
    }
}
