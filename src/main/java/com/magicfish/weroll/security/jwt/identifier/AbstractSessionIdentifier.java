package com.magicfish.weroll.security.jwt.identifier;

import com.magicfish.weroll.config.property.SessProperties;
import com.magicfish.weroll.exception.IllegalSessionTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.util.Map;

public class AbstractSessionIdentifier implements SessionIdentifier {

    public AbstractSessionIdentifier(SessProperties properties) {

        this.properties = properties;
    }

    protected SessProperties properties;

    protected String getIdentify(String secretKey, String token) {
        Claims body = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();

        String id = body.get("id", String.class);
        Long time = body.get("time", Long.class);

        return generateIdentify(id, time);
    }

    @Override
    public String generateIdentify(String userid, Long time) {
        if (properties.isOnePointEnter()) {
            return userid;
        }
        return userid + "_" + String.valueOf(time);
    }

    @Override
    public void saveUserPayload(String secretKey, String token, Map<String, Object> params, Long tokenExpireTime) {

    }

    @Override
    public UserPayload getUserPayload(String secretKey, String token) throws IllegalSessionTokenException {
        return null;
    }
}