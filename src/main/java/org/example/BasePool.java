package org.example;

import org.apache.log4j.Logger;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 *  Mysql连接池接口
 */
public interface BasePool<T> {

    static final Logger logger = Logger.getLogger(App.class);

    //连接池初始化
    void init();

    //获取连接
    T getConnection();

    //释放连接
    void freeConnection(T connection);

    //创建连接
    T createConnection();

    //关闭连接
    void closeConnection();
}
