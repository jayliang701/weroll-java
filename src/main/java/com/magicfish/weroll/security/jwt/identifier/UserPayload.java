package com.magicfish.weroll.security.jwt.identifier;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class UserPayload extends User {

    public static UserPayload build(String id, Collection<? extends GrantedAuthority> authorities, Map<String, Object> source) {
        return new UserPayload(id, authorities, source);
    }

    public static UserPayload build(String id, Object role, Map<String, Object> source) {
        Collection<SimpleGrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(String.valueOf(role)));
        return build(id, roles, source);
    }

    public static UserPayload build(String id, Map<String, Object> source) {
        return build(id, "*", source);
    }

    public static UserPayload buildWithUsernameAndPassword(String username, String password, Collection<? extends GrantedAuthority> authorities, Map<String, Object> source) {
        return new UserPayload(username, password, authorities, source);
    }

    public static UserPayload buildWithUsernameAndPassword(String username, String password, Object role, Map<String, Object> source) {
        Collection<SimpleGrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(String.valueOf(role)));
        return buildWithUsernameAndPassword(username, password, roles, source);
    }

    public static UserPayload buildWithUsernameAndPassword(String username, String password, Object role) {
        return buildWithUsernameAndPassword(username, password, role, new HashMap<>());
    }

    public static UserPayload buildWithUsernameAndPassword(String username, String password, Map<String, Object> source) {
        return buildWithUsernameAndPassword(username, password, "*", source);
    }

    public UserPayload(String username, String password, Collection<? extends GrantedAuthority> authorities, Map<String, Object> source) {
        super(username, password, authorities);
        this.source = source;
    }

    public UserPayload(String id, Collection<? extends GrantedAuthority> authorities, Map<String, Object> source) {
        super(id, id, authorities);
        this.id = id;
        this.source = source;
    }

    private Map<String, Object> source = new HashMap<>();

    private String id;

    public String getId() {
        return id;
    }

    public Object getParam(String key) {
        return source.get(key);
    }

    public boolean hasParam(String key) {
        return source.containsKey(key);
    }

    public UserPayload withId(String id) {
        this.id = id;
        return this;
    }
}
