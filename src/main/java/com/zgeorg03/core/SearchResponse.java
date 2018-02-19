package com.zgeorg03.core;

import java.util.List;

public class SearchResponse {
    private  String msg;
    private int count;
    private long responseTime;
    private String query;
    private List<DocumentInfo> content;

    public SearchResponse(String msg){
        this.msg = msg;
    }
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setContent(List<DocumentInfo> content) {
        this.content = content;
    }

    public List<DocumentInfo> getContent() {
        return content;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }

    public int getCount() {
        return count;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
