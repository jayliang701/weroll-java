package com.magicfish.weroll.security.jwt;

import com.magicfish.weroll.config.GlobalSetting;
import com.magicfish.weroll.config.property.SessProperties;
import com.magicfish.weroll.exception.IllegalSessionTokenException;
import com.magicfish.weroll.security.jwt.identifier.SessionIdentifier;
import com.magicfish.weroll.security.jwt.identifier.UserPayload;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class SessionTokenProvider {

    @Autowired
    private GlobalSetting globalSetting;

    private SessionIdentifier identifier;

    private String secretKey;

    @PostConstruct
    protected void init() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        secretKey = Base64.getEncoder().encodeToString(globalSetting.getSess().getSecret().getBytes());

        SessProperties sessProperties = globalSetting.getSess();
        Class engineClass = sessProperties.getStorageEngine().getEngineClass();

        Constructor constructor = engineClass.getConstructor(new Class[] { SessProperties.class });
        identifier = (SessionIdentifier) constructor.newInstance(sessProperties);
    }

    public String createToken(String userid) {
        Long time = System.currentTimeMillis();
        return createToken(userid, time, new HashMap<>());
    }

    public String createToken(String userid, Long time) {
        return createToken(userid, time, new HashMap<>());
    }

    public String createToken(String userid, Map<String, Object> params) {
        Long time = System.currentTimeMillis();
        return createToken(userid, time, params);
    }

    public String createToken(String userid, Long time, Map<String, Object> params) {
        String identify = identifier.generateIdentify(userid, time);

        Map<String, Object> more = new HashMap<>();
        Claims claims = Jwts.claims().setSubject(identify);
        for (Map.Entry<String, Object> pair : params.entrySet()) {
            claims.put(pair.getKey(), pair.getValue());
            more.put(pair.getKey(), pair.getValue());
        }
        claims.put("id", userid);
        claims.put("time", time);
        more.put("userid", userid);
        more.put("time", time);

        Long tokenExpireTime = globalSetting.getSess().getTokenExpireTime();

        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenExpireTime * 1000);

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        identifier.saveUserPayload(secretKey, token, more, tokenExpireTime);
        return token;
    }

    public Authentication getAuthentication(String token) throws IllegalSessionTokenException {
        UserPayload payload = identifier.getUserPayload(secretKey, token);
        return new UsernamePasswordAuthenticationToken(payload, "", payload.getAuthorities());
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }

        Cookie cookie = WebUtils.getCookie(req, "authorization");
        if (cookie != null) {
            bearerToken = cookie.getValue();
        }

        return bearerToken;
    }

    public boolean validateToken(String token) throws IllegalSessionTokenException {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new IllegalSessionTokenException();
        }
    }

}
