package org.example;

import java.time.Duration;

/**
 *  Redis连接池配置
 *
 * */
public class RedisPoolConfig extends BaseConfig{

//    //ip地址
//    private String ip;
//
//    //端口
//    private int port;
//
    //密码
    private String password;
//
//    //最大连接数
//    private int maxTotal = 20;
//
//    //初始化数
//    private int initTotal = 10;
//
//    //最大等待时间
//    private long maxWaitTime = 7;

    //返回Redis时验证是否可用
    private boolean testOnBorrow = false;

    public RedisPoolConfig(){
        this.ip = "127.0.0.1";
        this.port = 6379;
    }

    public RedisPoolConfig(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    public RedisPoolConfig(String ip, int port, String password){
        this.ip = ip;
        this.port = port;
        this.password = password;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getMaxIdle() {
        return initTotal;
    }

    public void setMaxIdle(int initTotal) {
        this.initTotal = initTotal;
    }

    public long getMaxWaitTime() {
        return maxWaitTime;
    }

    public void setMaxWaitTime(long maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }
}
