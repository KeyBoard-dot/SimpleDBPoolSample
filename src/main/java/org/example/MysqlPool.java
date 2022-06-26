package org.example;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Mysql连接池的实现
 *
 * */
public class MysqlPool implements BasePool<Connection>{

    //连接池配置
    private MysqlPoolConfig mysqlPoolConfig;

    //连接池空闲队列
    private Queue<Connection> freeConnectionQueue = null;

    //连接池活动队列
    private Queue<Connection> activeConnectionQueue = null;

    //记录队列的连接总数
    private AtomicInteger recordCount = new AtomicInteger();

    //初始化数据库连接数
    private int initTotal;

    //数据库最大连接数
    private int maxTotal;

    //数据库最长等待时间
    private long maxWaitTime;

    public MysqlPool(MysqlPoolConfig mysqlPoolConfig){
        this.mysqlPoolConfig = mysqlPoolConfig;
        //注册驱动
        MysqlPoolConfig.driver(mysqlPoolConfig.getDriverName());
        initTotal = mysqlPoolConfig.getInitTotal();
        maxTotal = mysqlPoolConfig.getMaxTotal();
        maxWaitTime = mysqlPoolConfig.getMaxWaitTime();
        logger.info("连接池实例化完成");
    }

    //连接池初始化
    @Override
    public void init() {
        if(initTotal > maxTotal){
            maxTotal = initTotal;
            logger.warn("Warning: 最大连接数小于初始化连接数时,将最大连接数修改为初始化连接数");
        }
        freeConnectionQueue = new LinkedBlockingDeque<>(initTotal);
        activeConnectionQueue = new LinkedBlockingDeque<>(maxTotal);
        for(int i = 0; i < initTotal; i++){
            this.freeConnectionQueue.offer(createConnection());
        }
        logger.info("数据库连接池初始化完成");

    }

    //1.获取当前毫秒 计算时间是否超时
    //2.判断队列当中是否还有空闲连接
    //3.如果有连接，获取连接进行返回
    //4.如果没有连接，判断是否达到最大连接数
    //5.如果达到最大连接数量，就进行等待
    //6.如果没有达到最大连接数量，进行创建一个连接
    //7.放入使用队列当中，然后进行返回
    //8.如果连接超时的话，就进行抛出异常
    @Override
    public Connection getConnection() {
        long startTime = System.currentTimeMillis();
        Connection connection = null;
        while (true){
            //从空闲队列获取
            connection = freeConnectionQueue.poll();
            if(connection != null){
                this.activeConnectionQueue.offer(connection);
                recordCount.incrementAndGet();
                return connection;
            }

            //如果连接数未超过最大连接数则创建
            if(recordCount.get() < maxTotal) {
                connection = createConnection();
                this.activeConnectionQueue.offer(connection);
                return connection;
            }

            if((System.currentTimeMillis() - startTime) > maxWaitTime) throw new MysqlConnectionException("连接超时");
        }
    }

    //释放活动队列的Connection
    @Override
    public void freeConnection(Connection connection) {
        if(this.activeConnectionQueue.remove(connection)){
            recordCount.decrementAndGet();
            this.freeConnectionQueue.offer(connection);
        }
    }

    //创建Connection连接
    @Override
    public Connection createConnection() {
        String username = mysqlPoolConfig.getUsername();
        String password = mysqlPoolConfig.getPassword();
        String url = mysqlPoolConfig.getUrl();
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        recordCount.incrementAndGet();
        logger.info("已成功初始化一个Mysql Connection: " + "IP: " + mysqlPoolConfig.getIp() + "Port: " + mysqlPoolConfig.getPort() +
                "Database Name: " + mysqlPoolConfig.getDatabaseName());
        return connection;
    }

    //关闭Connection连接
    @Override
    public void closeConnection() {
        closeConnection(this.freeConnectionQueue);
        closeConnection(this.activeConnectionQueue);
    }

    public void closeConnection(Queue<Connection> connectionQueue){
        Connection connection = connectionQueue.poll();
        if(connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                try {
                    if(!connection.isClosed()){
                        connectionQueue.offer(connection);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            finally {
                closeConnection(connectionQueue);
            }
        }
    }


}
