package com.magicfish.weroll.config.property;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AuthProperties extends AbstractProperties {

    public static String BCRYPT_ENCODE = "bcrypt";

    public static String MD5_ENCODE = "md5";

    private Set<String> publicPaths;

    public Set<String> getPublicPaths() {
        return publicPaths;
    }

    private String whitelist;

    public String getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(String whitelist) {
        this.whitelist = whitelist;

        List<String> paths = Arrays.asList(whitelist.split(","));
        publicPaths = new HashSet<>();

        paths.forEach(path -> publicPaths.add(path.trim()));
    }

    private boolean enabled = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    private String deniedRedirect;

    public String getDeniedRedirect() {
        return deniedRedirect;
    }

    public void setDeniedRedirect(String deniedRedirect) {
        this.deniedRedirect = deniedRedirect;
    }

    private String entryPoint;

    public String getEntryPoint() {
        return entryPoint;
    }

    public void setEntryPoint(String entryPoint) {
        this.entryPoint = entryPoint;
    }

    public boolean isPublicPath(String path) {
        return publicPaths.contains(path);
    }

    private String passwordEncodeSalt = "";

    public String getPasswordEncodeSalt() {
        return passwordEncodeSalt;
    }

    public void setPasswordEncodeSalt(String passwordEncodeSalt) {
        this.passwordEncodeSalt = passwordEncodeSalt;
    }

    private int passwordEncodeStrength = 12;

    public int getPasswordEncodeStrength() {
        return passwordEncodeStrength;
    }

    public void setPasswordEncodeStrength(int passwordEncodeStrength) {
        this.passwordEncodeStrength = passwordEncodeStrength;
    }

    private String passwordEncodeMethod = BCRYPT_ENCODE;

    public String getPasswordEncodeMethod() {
        return passwordEncodeMethod;
    }

    public void setPasswordEncodeMethod(String passwordEncodeMethod) {
        if (!BCRYPT_ENCODE.equals(passwordEncodeMethod) && !MD5_ENCODE.equals(passwordEncodeMethod)) {
            throw new IllegalArgumentException("Unsupported encode method \"" + passwordEncodeMethod + "\"");
        }
        this.passwordEncodeMethod = passwordEncodeMethod;
    }
}
