package com.mybatis.my_mybatis.configuration;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 读取与解析配置信息，并返回处理后的Environment
 */
public class Configuration {

    //获取类加载器，SystemClassLoader负责加载应用指定的类 (环境变量classpath中配置的内容)
    private static ClassLoader loader = ClassLoader.getSystemClassLoader();

    /**
     * 加载资源
     *
     * @param resource
     * @return
     */
    public Connection build(String resource) {
        try {
            //加载指定资源文件
            InputStream inputStream = loader.getResourceAsStream(resource);
            //解析xml文件
            SAXReader reader = new SAXReader();
            //读取Document对象
            Document document = reader.read(inputStream);
            //获取根标签
            Element rootElement = document.getRootElement();
            return evalDataSource(rootElement);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("error occured while evaling xml " + resource);
        }
    }

    /**
     * 读取数据库配置文件
     *
     * @param element
     * @return
     * @throws ClassNotFoundException
     */
    private Connection evalDataSource(Element element) throws ClassNotFoundException {
        //如果根标签不是databases抛异常
        if (!element.getName().equals("database")) {
            throw new RuntimeException("root should be <database>");
        }
        String driverClassName = null;
        String url = null;
        String username = null;
        String password = null;

        for (Object object : element.elements("property")) {
            Element elm1 = (Element) object;
            //获取对应的键值对
            String name = elm1.attributeValue("name");
            String value = getValue(elm1);
            if (name == null || value == null) {
                throw new RuntimeException("[database]: <property> should contain name and value");
            }
            //赋值
            switch (name) {
                case "url":
                    url = value;
                    break;
                case "username":
                    username = value;
                    break;
                case "password":
                    password = value;
                    break;
                case "driverClassName":
                    driverClassName = value;
                    break;
                default:
                    throw new RuntimeException("[database]: <property> unknown name");
            }
        }

        //加载驱动
        Class.forName(driverClassName);
        Connection connection = null;
        try {
            //建立数据库链接
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * 获取property属性的值,如果有value值,则读取 没有设置value,则读取内容
     *
     * @param node
     * @return
     */
    private String getValue(Element node) {
        return node.hasContent() ? node.getText() : node.attributeValue("value");
    }

    /**
     * 读取mapper配置文件
     *
     * @param path
     * @return
     */
    @SuppressWarnings("rawtypes")
    public MapperBean readMapper(String path) {
        MapperBean mapper = new MapperBean();
        try {
            InputStream stream = loader.getResourceAsStream(path);
            SAXReader reader = new SAXReader();
            Document document = reader.read(stream);
            Element root = document.getRootElement();
            mapper.setInterfaceName(root.attributeValue("namespace").trim()); //把mapper节点的nameSpace值存为接口名
            List<Function> list = new ArrayList<>(); //用来存储方法的List
            for (Iterator rootIter = root.elementIterator(); rootIter.hasNext(); ) {//遍历根节点下所有子节点
                Function fun = new Function();    //用来存储一条方法的信息
                Element e = (Element) rootIter.next();
                String sqlType = e.getName().trim();
                String funcName = e.attributeValue("id").trim();
                String sql = e.getText().trim();
                String resultType = e.attributeValue("resultType").trim();
                fun.setSqltype(sqlType);
                fun.setFuncName(funcName);
                Object newInstance = null;
                try {
                    newInstance = Class.forName(resultType).newInstance();
                } catch (InstantiationException e1) {
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }
                fun.setResultType(newInstance);
                fun.setSql(sql);
                list.add(fun);
            }
            mapper.setList(list);

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return mapper;
    }
}
