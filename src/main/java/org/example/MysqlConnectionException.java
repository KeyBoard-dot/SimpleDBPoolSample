package org.example;

/**
*   Mysql连接异常
* */
public class MysqlConnectionException extends RuntimeException{
    public MysqlConnectionException(String message){
        super(message);
    }
}
