package com.magicfish.weroll.config.property;

import com.magicfish.weroll.config.property.enums.SessStorageEngineType;

import com.magicfish.weroll.config.property.common.RedisProperties;

public class SessProperties extends AbstractProperties {

    private boolean onePointEnter = false;

    public boolean isOnePointEnter() {
        return onePointEnter;
    }

    public void setOnePointEnter(boolean onePointEnter) {
        this.onePointEnter = onePointEnter;
    }

    private String secret;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    private long tokenExpireTime = 86400;  // second, default 1 day

    public long getTokenExpireTime() {
        return tokenExpireTime;
    }

    public void setTokenExpireTime(int tokenExpireTime) {
        this.tokenExpireTime = tokenExpireTime;
    }

    private RedisProperties redis = new RedisProperties();

    public RedisProperties getRedis() {
        return redis;
    }

    public void setRedis(RedisProperties redis) {
        this.redis = redis;
    }

    private MongoDBProperties mongodb = new MongoDBProperties();

    public MongoDBProperties getMongodb() {
        return mongodb;
    }

    public void setMongodb(MongoDBProperties mongodb) {
        this.mongodb = mongodb;
    }

    private SessStorageEngineType storageEngine = SessStorageEngineType.REDIS;

    public SessStorageEngineType getStorageEngine() {
        return storageEngine;
    }

    public void setStorageEngine(SessStorageEngineType storageEngine) {
        this.storageEngine = storageEngine;
    }
}
