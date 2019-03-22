package com.magicfish.weroll.config.property;

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
}
