package com.magicfish.weroll.config.property.enums;

import com.magicfish.weroll.security.jwt.identifier.SessionMongoDBIdentifier;
import com.magicfish.weroll.security.jwt.identifier.SessionRedisIdentifier;

public enum SessStorageEngineType {
    REDIS("redis", SessionRedisIdentifier.class),
    MONGODB("mongodb", SessionMongoDBIdentifier.class);

    private SessStorageEngineType(String name, Class<?> engineClass) {
        this.name = name;
        this.engineClass = engineClass;
    }

    private String name;

    public String getName() {
        return name;
    }

    private Class<?> engineClass;

    public Class<?> getEngineClass() {
        return engineClass;
    }
}
