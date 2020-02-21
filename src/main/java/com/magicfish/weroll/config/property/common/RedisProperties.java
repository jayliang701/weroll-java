package com.magicfish.weroll.config.property.common;

import com.magicfish.weroll.config.property.AbstractProperties;

public class RedisProperties extends AbstractProperties {
    private String host = "localhost";

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    private int port = 6379;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    private String pass = "";

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    private String prefix = "";

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    private RedisPoolProperties pool = new RedisPoolProperties();

    public RedisPoolProperties getPool() {
        return pool;
    }

    public void setPool(RedisPoolProperties pool) {
        this.pool = pool;
    }
}
