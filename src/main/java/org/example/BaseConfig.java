package org.example;

/**
 *  Mysql基础配置
 */
public class BaseConfig {

    protected static final String MYSQLURL_PRIFIEX = "jdbc:mysql://";

    //ip地址
    protected String ip;

    //端口
    protected int port;

    //初始化数
    protected int initTotal = 10;

    //最大连接数
    protected int maxTotal = 20;

    //最大等待时间
    protected long maxWaitTime = 7000;

    public static void driver(String className)
    {
        try {
            Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
