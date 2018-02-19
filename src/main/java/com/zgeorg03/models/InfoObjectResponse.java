package com.zgeorg03.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

public class InfoObjectResponse<T> {

    @ApiModelProperty(notes = "Message",value = "Hello World", required = true)
    @JsonProperty(value = "message")
    private String msg;

    @ApiModelProperty(notes = "Message",value = "Hello World", required = true)
    @JsonProperty(value = "data")
    private T data;


    public InfoObjectResponse(String msg,T data) {
        this.msg = msg;
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

