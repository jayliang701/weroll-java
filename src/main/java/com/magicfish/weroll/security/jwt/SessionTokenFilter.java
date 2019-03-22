package com.magicfish.weroll.security.jwt;

import com.magicfish.weroll.exception.IllegalSessionTokenException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class SessionTokenFilter extends GenericFilterBean {

    private SessionTokenProvider sessionTokenProvider;

    public SessionTokenFilter(SessionTokenProvider sessionTokenProvider) {
        this.sessionTokenProvider = sessionTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
            throws IOException, ServletException {
        String token = sessionTokenProvider.resolveToken((HttpServletRequest) req);
        try {
            boolean valid = sessionTokenProvider.validateToken(token);
            if (token != null && valid) {
                Authentication auth = sessionTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (IllegalSessionTokenException e) {
            // token expired or invalid or kicked out
        } catch (Exception e) {
            e.printStackTrace();
        }
        filterChain.doFilter(req, res);
    }

}
