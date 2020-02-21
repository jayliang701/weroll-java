package com.magicfish.weroll.config.property.common;

import com.magicfish.weroll.config.property.AbstractProperties;

public class RedisPoolProperties extends AbstractProperties {

    private int maxActive = 20;

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    private int maxWait = -1;

    public int getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(int maxWait) {
        this.maxWait = maxWait;
    }

    private int maxIdle = 10;

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    private int minIdle = 0;

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }
}
