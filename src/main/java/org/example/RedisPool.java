package org.example;

import com.mysql.cj.util.StringUtils;

import redis.clients.jedis.Jedis;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Redis连接池
 * */

public class RedisPool implements BasePool<Jedis>{

    private Jedis jedis;

    private RedisPoolConfig redisPoolConfig;

    //连接池空闲队列
    private Queue<Jedis> freeConnectionQueue = null;

    //连接池活动队列
    private Queue<Jedis> activeConnectionQueue = null;

    //记录队列的连接总数
    private AtomicInteger recordCount = new AtomicInteger();

    //初始化数据库连接数
    private int initTotal;

    //数据库最大连接数
    private int maxTotal;

    //数据库最长等待时间
    private long maxWaitTime;

    //返回Redis时验证是否可用
    private boolean testOnBorrow;


    public RedisPool(RedisPoolConfig redisPoolConfig){
        this.redisPoolConfig = redisPoolConfig;
        initTotal = redisPoolConfig.getMaxIdle();
        maxTotal = redisPoolConfig.getMaxTotal();
        maxWaitTime = redisPoolConfig.getMaxWaitTime();
        testOnBorrow = redisPoolConfig.isTestOnBorrow();
        logger.info("Redis连接池实例化完成");
    }

    @Override
    public void init() {
        if(initTotal > maxTotal){
            maxTotal = initTotal;
            logger.warn("Warning: 最大连接数小于初始化连接数时,将最大连接数修改为初始化连接数");
        }
        freeConnectionQueue = new LinkedBlockingQueue<>(initTotal);
        activeConnectionQueue = new LinkedBlockingQueue<>(maxTotal);
        for(int i = 0; i < initTotal; i++){
            freeConnectionQueue.offer(createConnection());
            recordCount.incrementAndGet();
        }
        logger.info("数据库连接池初始化完成");
    }

    @Override
    public Jedis getConnection() {
        long startTime = System.currentTimeMillis();
        Jedis jedis = null;
        while (true){

            jedis = freeConnectionQueue.poll();
            if(jedis != null){
                if(testOnBorrow){
                    if(isValidate(jedis)){
                        this.activeConnectionQueue.offer(jedis);
                        recordCount.incrementAndGet();
                        return jedis;
                    }
                    throw new RedisConnectionException("Redis连接无效");
                }
                else{
                    this.activeConnectionQueue.offer(jedis);
                    recordCount.incrementAndGet();
                    return jedis;
                }

            }

            if(recordCount.get() < maxTotal){
                jedis = createConnection();
                if(testOnBorrow){
                    if(isValidate(jedis)){
                        this.activeConnectionQueue.offer(jedis);
                        return jedis;
                    }
                    throw new RedisConnectionException("Redis连接无效");
                }else {
                    this.activeConnectionQueue.offer(jedis);
                    return jedis;
                }

            }

            if((System.currentTimeMillis() - startTime) > maxWaitTime) {
                throw new MysqlConnectionException("连接超时");
            }
        }

    }

    @Override
    public void freeConnection(Jedis connection) {
        if(activeConnectionQueue.remove(connection)){
            this.freeConnectionQueue.offer(connection);
            recordCount.decrementAndGet();
        }
    }

    @Override
    public Jedis createConnection() {
        Jedis jedis = new Jedis(redisPoolConfig.getIp(), redisPoolConfig.getPort());
        String password = redisPoolConfig.getPassword();
        if(!StringUtils.isNullOrEmpty(password)){
            jedis.auth(password);
        }
        recordCount.incrementAndGet();
        return jedis;
    }

    @Override
    public void closeConnection() {
        closeConnection(freeConnectionQueue);
        closeConnection(activeConnectionQueue);
    }


    public boolean isValidate(Jedis jedis){
        String ping = jedis.ping();
        if(ping.equals("PONG")){
            return true;
        }
        return false;
    }

    private void closeConnection(Queue<Jedis> connectionQueue) {
        for(int i = 0; i < connectionQueue.size(); i++){
            Jedis jedis = connectionQueue.poll();
            if(jedis != null){
                jedis.close();
            }
        }

    }


}
