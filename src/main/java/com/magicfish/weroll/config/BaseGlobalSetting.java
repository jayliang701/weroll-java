package com.magicfish.weroll.config;

import com.magicfish.weroll.config.property.*;

public class BaseGlobalSetting {

    private APIProperties api;

    public APIProperties getApi() {
        return api;
    }

    public void setApi(APIProperties api) {
        this.api = api;
    }

    private RouterProperties router;

    public RouterProperties getRouter() {
        return router;
    }

    public void setRouter(RouterProperties router) {
        this.router = router;
    }

    private AuthProperties auth;

    public AuthProperties getAuth() {
        return auth;
    }

    public void setAuth(AuthProperties auth) {
        this.auth = auth;
    }

    private ThreadProperties thread = new ThreadProperties();

    public ThreadProperties getThread() {
        return thread;
    }

    public void setThread(ThreadProperties thread) {
        this.thread = thread;
    }

    private SessProperties sess;

    public SessProperties getSess() {
        return sess;
    }

    public void setSess(SessProperties sess) {
        this.sess = sess;
    }

    private ResProperties res;

    public ResProperties getRes() {
        return res;
    }

    public void setRes(ResProperties res) {
        this.res = res;
    }

    private SiteProperties site;

    public SiteProperties getSite() {
        return site;
    }

    public void setSite(SiteProperties site) {
        this.site = site;
    }
}