package com.mybatis.web;

import com.mybatis.my_mybatis.sqlSession.SQLSession;
import com.mybatis.web.entity.UserEntity;
import com.mybatis.web.mapper.UserMapper;

/**
 * 测试
 */
public class TestMybatis {
    public static void main(String[] args) {
        SQLSession sqlSession = new SQLSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        UserEntity userById = mapper.getUserById(1);
        System.err.println(userById);
    }
}
