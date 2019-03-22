package com.magicfish.weroll.config.property;

public class SessProperties extends AbstractProperties {

    private boolean onePointEnter = false;

    public boolean isOnePointEnter() {
        return onePointEnter;
    }

    public void setOnePointEnter(boolean onePointEnter) {
        this.onePointEnter = onePointEnter;
    }

    private boolean useRedis;

    public boolean getUseRedis() {
        return useRedis;
    }

    public void setUseRedis(boolean useRedis) {
        this.useRedis = useRedis;
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
}
