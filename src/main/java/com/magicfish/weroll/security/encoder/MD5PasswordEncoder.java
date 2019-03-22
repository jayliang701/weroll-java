package com.magicfish.weroll.security.encoder;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

public class MD5PasswordEncoder implements PasswordEncoder {

    private String salt;

    public MD5PasswordEncoder(String salt) {
        if (salt == null) salt = "";
        this.salt = salt;
    }

    @Override
    public String encode(CharSequence charSequence) {
        return DigestUtils.md5Hex(salt + charSequence.toString());
    }

    @Override
    public boolean matches(CharSequence charSequence, String s) {
        return encode(charSequence).equals(s);
    }
}
