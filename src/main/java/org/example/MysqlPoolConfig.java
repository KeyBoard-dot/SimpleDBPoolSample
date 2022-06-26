package org.example;

import com.mysql.cj.util.StringUtils;
import com.mysql.cj.x.protobuf.MysqlxNotice;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Mysql连接池的配置
 * */
public class MysqlPoolConfig extends BaseConfig {

    //url: jdbc:mysql://ip:port/databaseName?databaseParam

    private String driverName = "com.mysql.cj.jdbc.Driver";     //数据库驱动名
    private String username;                                    //数据库用户名
    private String password;                                    //数据库密码
//    private String ip;                                          //数据库IP
//    private int port;                                           //数据库端口
    private String databaseName;                                //数据库名
    private String databaseParam;                               //数据库连接参数
    private String url;                                         //数据库URL
//    private int initTotal = 10;                                 //初始化数据库连接数
//    private int maxTotal = 20;                                  //数据库最大连接数
//    private long maxWaitTime = 7000;                            //数据库最长等待时间 单位毫秒

    private static final String ipRegex = "((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d{1}|[1-9])\\.)" +
            "((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)\\.){2}" +
            "(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d{1}|\\d)";
    private static final String portRegex = ":([1-9]|[1-9]\\d|[1-9]\\d{2}|[1-9]\\d{3}|[1-9]\\d{4}|[6[0-5][0-5][0-3][0-5]])/";

    private static final String databaseNameRegex = "\\w+\\?";

    private static final String databaseParamRegex = "\\?.{1,}$";

    public MysqlPoolConfig(){
        super();
    }

    public MysqlPoolConfig(String url) {
        setUrl(url);
    }

    public MysqlPoolConfig(String url, String username, String password){
        this(url);
        if(StringUtils.isNullOrEmpty(username) || StringUtils.isNullOrEmpty(password)){
            throw new MysqlConfigException("用户名和密码为null或者为空");
        }
        this.username = username;
        this.password = password;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIp() {
        if(StringUtils.isNullOrEmpty(ip)){
            throw new MysqlConfigException("ip地址为null或为空");
        }
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        if(port <= 0){
            throw new MysqlConfigException("端口不正确");
        }
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDatabaseName() {
        if(StringUtils.isNullOrEmpty(databaseName)){
            throw new MysqlConfigException("数据库名为null或为空");
        }
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabaseParam() {
        if(StringUtils.isNullOrEmpty(databaseParam)){
            throw new MysqlConfigException("数据库参数为null或为空");
        }
        return databaseParam;
    }

    public void setDatabaseParam(String databaseParam) {
        this.databaseParam = databaseParam;
    }

    public int getInitTotal() {
        return initTotal;
    }

    public void setInitTotal(int initTotal) {
        this.initTotal = initTotal;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public String getUrl() {
        StringBuilder urlBuilder = new StringBuilder();
        if(StringUtils.isNullOrEmpty(url)){
            if(databaseParam.isEmpty()){
                urlBuilder.append(MYSQLURL_PRIFIEX + ip + ":" + port + "/" + databaseName);
            }
            else{
                urlBuilder.append(MYSQLURL_PRIFIEX + ip + ":" + port + "/" + databaseName + "?" + databaseParam);
            }
            return urlBuilder.toString();
        }
        else{
            return url;
        }

    }

    public void setUrl(String url) {
        if(StringUtils.isNullOrEmpty(url)){
            throw new MysqlConfigException("url为null或者没有任何字符");
        }
        this.url = url;
        //获取ip
        Pattern pattern = Pattern.compile(ipRegex);
        Matcher matcher = pattern.matcher(url);
        if(matcher.find()){
            this.ip = matcher.group();
        }

        //获取端口
        pattern = Pattern.compile(portRegex);
        matcher = pattern.matcher(url);
        if(matcher.find()){
            String result = matcher.group();
            result = result.substring(1, result.length() - 1);
            this.port = Integer.parseInt(result);
        }


        //获取数据库名
        pattern = Pattern.compile(databaseNameRegex);
        matcher = pattern.matcher(url);
        if(matcher.find()){
            String result = matcher.group();
            result = result.substring(0, result.length() - 1);
            this.databaseName = result;
        }

        //获取数据库连接参数
        pattern = Pattern.compile(databaseParamRegex);
        matcher = pattern.matcher(url);
        if(matcher.find()){
            String result = matcher.group();
            result = result.substring(1);
            this.databaseParam = result;
        }

    }

    public long getMaxWaitTime() {
        return maxWaitTime;
    }

    public void setMaxWaitTime(long maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
    }
}
