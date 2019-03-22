package com.magicfish.weroll.security.jwt;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class SessionTokenFilterConfigure extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private SessionTokenProvider sessionTokenProvider;

    public SessionTokenFilterConfigure(SessionTokenProvider sessionTokenProvider) {
        this.sessionTokenProvider = sessionTokenProvider;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        SessionTokenFilter customFilter = new SessionTokenFilter(sessionTokenProvider);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
