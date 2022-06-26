package org.example;

import redis.clients.jedis.Jedis;

import java.sql.*;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * App Class
 *
 */
public class App 
{

    public static void main( String[] args )
    {
        //url: jdbc:mysql://127.0.0.1:3306/hello?serverTimezone=UTC&characterEncoding=utf-8
//        MysqlPoolConfig mysqlPoolConfig = new MysqlPoolConfig("jdbc:mysql://ip:3306/hello?serverTimezone=UTC&characterEncoding=utf-8",
//                "root", "hello");
//        System.out.println("ip: " + mysqlPoolConfig.getIp());
//        System.out.println("port: " + mysqlPoolConfig.getPort());
//        System.out.println("databaseName: " + mysqlPoolConfig.getDatabaseName());
//        System.out.println("databaseParam: " + mysqlPoolConfig.getDatabaseParam());
//        mysqlPoolConfig.setUsername("root");
//        mysqlPoolConfig.setPassword("hello");
//        mysqlPoolConfig.setDriverName("com.mysql.cj.jdbc.Driver");
//        mysqlPoolConfig.setIp("172.23.60.2");
//        mysqlPoolConfig.setPort(3306);
//        mysqlPoolConfig.setDatabaseName("password");
//        mysqlPoolConfig.setDatabaseParam("serverTimezone=UTC&characterEncoding=utf-8");
//        mysqlPoolConfig.setInitTotal(10);
//        mysqlPoolConfig.setMaxTotal(20);
//        mysqlPoolConfig.setMaxWaitTime(10);

//        BasePool<Connection> pool = new MysqlPool(mysqlPoolConfig);
//        pool.init();
//
//        String sql = "select * from employee";
//        for(int i = 0; i < 10; i++){
//            Connection connection =  pool.getConnection();
//            try {
//                ResultSet resultSet = connection.prepareStatement(sql).executeQuery();
//                if(resultSet.next()){
//                    String result = resultSet.getString("school");
//                    System.out.println("result = " + result);
//                }
//                pool.freeConnection(connection);
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//        pool.closeConnection();

        RedisPoolConfig redisPoolConfig = new RedisPoolConfig();
        redisPoolConfig.setTestOnBorrow(true);
        RedisPool redisPool = new RedisPool(redisPoolConfig);

        redisPool.init();
        for(int i = 0; i < 10; i++){
            Jedis connection =  redisPool.getConnection();
            System.out.println(connection.ping());
            redisPool.freeConnection(connection);
        }
        redisPool.closeConnection();
    }
}
