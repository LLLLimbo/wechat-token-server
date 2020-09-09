package com.planet.token.cache;

import org.springframework.stereotype.Service;

@Service
public interface CacheService {

    String getTokenByAppId(String appId);

    void refreshToken(String appId, String token);

}
