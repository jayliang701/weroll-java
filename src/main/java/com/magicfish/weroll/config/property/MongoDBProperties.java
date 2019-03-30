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

    private boolean autoBuildIndex = true;

    public boolean isAutoBuildIndex() {
        return autoBuildIndex;
    }

    public void setAutoBuildIndex(boolean autoBuildIndex) {
        this.autoBuildIndex = autoBuildIndex;
    }
}
