package com.magicfish.weroll.config.property;

public class MongoDBProperties {

    private String uri;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    private String table = "sess";

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    private String dbname = "weroll_sess";

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    private boolean autoBuildIndex = true;

    public boolean isAutoBuildIndex() {
        return autoBuildIndex;
    }

    public void setAutoBuildIndex(boolean autoBuildIndex) {
        this.autoBuildIndex = autoBuildIndex;
    }
}
