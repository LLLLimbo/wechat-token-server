package com.planet.token.entity;

import lombok.Data;

@Data
public class CreateConfigRequest {

    private String appId;

    private String secret;

    private Long refreshInterval;

}
