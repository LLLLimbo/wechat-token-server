package com.wechat.token.controller;

import com.wechat.token.schedule.ScheduleHelper;
import com.wechat.token.service.TokenService;
import com.wechat.token.entity.CreateConfigRequest;
import io.netty.util.internal.StringUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.ConnectException;
import java.util.Date;

@RestController
@RequestMapping(value = "/wechat/token")
public class TokenController {

    private final TokenService tokenService;
    private final ScheduleHelper scheduleHelper;

    public TokenController(TokenService tokenService, ScheduleHelper scheduleHelper) {
        this.tokenService = tokenService;
        this.scheduleHelper = scheduleHelper;
    }

    @PostMapping("/createConfig")
    public ResponseEntity<String> createConfig(@RequestBody CreateConfigRequest request) throws ConnectException {
        String token = tokenService.createConfig(request);
        if (token != null) {
            Date now = new Date();
            scheduleHelper.alterJob(request.getAppId(), now, request.getRefreshInterval(), now);
        }
        return ResponseEntity.ok(token != null ? token : "");
    }

    @PostMapping("/alterConfig")
    public ResponseEntity<String> alterConfig(@RequestBody CreateConfigRequest request) throws ConnectException {
        String token = tokenService.alterConfig(request);
        if (token != null) {
            Date now = new Date();
            scheduleHelper.alterJob(request.getAppId(), now, request.getRefreshInterval(), now);
        }
        return ResponseEntity.ok(token != null ? token : "");
    }

    @PostMapping("/getToken/{appId}")
    public String getToken(@PathVariable(name = "appId") String appId) throws ConnectException {
        if (StringUtil.isNullOrEmpty(appId)) {
            return null;
        }
        return tokenService.getToken(appId);
    }

    @PostMapping("/refreshAndGetToken/{appId}")
    public String refreshAndGetToken(@PathVariable(name = "appId") String appId) throws ConnectException {
        if (StringUtil.isNullOrEmpty(appId)) {
            return null;
        }
        String token = tokenService.refreshAndGetToken(appId);
        if (token != null) {
            Date now = new Date();
            scheduleHelper.alterJob(appId, now, null, now);
        }
        return token;
    }

    @PostMapping("/refreshToken/{appId}")
    public void refreshToken(@PathVariable(name = "appId") String appId) throws ConnectException {
        tokenService.refreshToken(appId);
    }
}
