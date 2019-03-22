package com.magicfish.weroll.config.property;

public class ThreadProperties extends AbstractProperties {

    private Integer corePoolSize = 10;

    public Integer getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(Integer corePoolSize) {
        this.corePoolSize = corePoolSize;
    }
}
