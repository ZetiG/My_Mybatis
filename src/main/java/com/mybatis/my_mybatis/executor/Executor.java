package com.mybatis.my_mybatis.executor;

/**
 * 执行器接口
 */
public interface Executor {
    //查询方法
    <T> T query(String statement, Object parameter);
}
