package com.magicfish.weroll.security.jwt.identifier;

import com.magicfish.weroll.exception.IllegalSessionTokenException;

import java.util.Map;

public interface SessionIdentifier {

    String generateIdentify(String userid, Long time);

    void saveUserPayload(String secretKey, String token, Map<String, Object> params, Long tokenExpireTime);

    UserPayload getUserPayload(String secretKey, String token) throws IllegalSessionTokenException;
}
