package org.example;

/**
 * RedisConnection异常类
 * */
public class RedisConnectionException extends RuntimeException{

    public RedisConnectionException(String message){
        super(message);
    }
}
