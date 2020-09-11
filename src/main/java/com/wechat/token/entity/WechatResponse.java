package com.wechat.token.entity;

import lombok.Data;

@Data
public class WechatResponse {

    private String access_token;

    private Integer expires_in;

    private Integer errcode;

    private String errmsg;

}
