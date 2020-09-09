package com.planet.token.schedule;

import com.planet.token.service.TokenService;
import org.springframework.stereotype.Component;

import java.net.ConnectException;

@Component
public class RefreshTokenActJob implements BeanJob{

    private final TokenService tokenService;

    public RefreshTokenActJob(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public void executeBeanJob(String param) {
        try {
            tokenService.refreshToken(param);
        } catch (ConnectException e) {
            e.printStackTrace();
        }
    }
}
