package com.planet.token.service;

import entity.CreateConfigRequest;
import org.springframework.stereotype.Service;

import java.net.ConnectException;

@Service
public interface TokenService {

    String createConfig(CreateConfigRequest request) throws ConnectException;

    String alterConfig(CreateConfigRequest request) throws ConnectException;

    String getToken(String appId) throws ConnectException;

    String refreshAndGetToken(String appId) throws ConnectException;

    void refreshToken(String appId) throws ConnectException;
}
