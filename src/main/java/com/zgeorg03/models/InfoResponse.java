package com.zgeorg03.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

public class InfoResponse {

    @ApiModelProperty(notes = "Message",value = "Hello World", required = true)
    @JsonProperty(value = "message")
    private String msg;


    public InfoResponse(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

