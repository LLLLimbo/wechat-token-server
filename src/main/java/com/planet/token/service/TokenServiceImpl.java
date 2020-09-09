package com.planet.token.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.planet.token.cache.CacheService;
import com.planet.token.model.Ticket;
import com.planet.token.repo.TicketRepo;
import com.planet.token.entity.CreateConfigRequest;
import com.planet.token.entity.WechatResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.ConnectException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
@DubboService(group = "TokenServerGroup")
public class TokenServiceImpl implements TokenService {

    private final TicketRepo ticketRepo;
    private final CacheService cacheService;
    private final RestTemplate restTemplate;
    private static final String WECHAT_TOKEN_URI = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";

    public TokenServiceImpl(TicketRepo ticketRepo, CacheService cacheService, RestTemplate restTemplate) {
        this.ticketRepo = ticketRepo;
        this.cacheService = cacheService;
        this.restTemplate = restTemplate;
    }

    @Override
    public String createConfig(CreateConfigRequest request) throws ConnectException {
        String uri = String.format(WECHAT_TOKEN_URI, request.getAppId(), request.getSecret());
        WechatResponse response = restTemplate.getForEntity(uri, WechatResponse.class).getBody();
        if (response == null) {
            throw new ConnectException("An unexpected error occurred requesting access token from wechat server.");
        }
        if (response.getAccess_token() != null) {
            Date now = new Date();
            Ticket ticket = new Ticket();
            ticket.setId(IdUtil.fastSimpleUUID());
            ticket.setAppId(request.getAppId());
            ticket.setRefreshInterval(request.getRefreshInterval());
            ticket.setSecret(request.getSecret());
            ticket.setToken(response.getAccess_token());
            ticket.setExpiredAt(DateUtil.offsetSecond(now, response.getExpires_in()));
            ticket.setLastRefreshedAt(now);
            ticket.setCreatedAt(now);
            ticketRepo.save(ticket);
            cacheService.refreshToken(request.getAppId(), response.getAccess_token());

            return response.getAccess_token();
        }
        log.error("An error occurred requesting access token from wechat server. Message ={}", response.getErrmsg());
        return null;
    }

    @Override
    public String alterConfig(CreateConfigRequest request) throws ConnectException {
        String uri = String.format(WECHAT_TOKEN_URI, request.getAppId(), request.getSecret());
        WechatResponse response = restTemplate.getForEntity(uri, WechatResponse.class).getBody();
        if (response == null) {
            throw new ConnectException("An unexpected error occurred requesting access token from wechat server.");
        }
        if (response.getAccess_token() != null) {
            Date now = new Date();
            Ticket ticket = ticketRepo.findByAppId(request.getAppId());
            ticket.setId(IdUtil.fastSimpleUUID());
            ticket.setAppId(request.getAppId());
            ticket.setRefreshInterval(request.getRefreshInterval());
            ticket.setSecret(request.getSecret());
            ticket.setToken(response.getAccess_token());
            ticket.setExpiredAt(DateUtil.offsetSecond(now, response.getExpires_in()));
            ticket.setLastRefreshedAt(now);
            ticket.setCreatedAt(now);
            ticketRepo.save(ticket);
            cacheService.refreshToken(request.getAppId(), response.getAccess_token());

            return response.getAccess_token();
        }
        log.error("An error occurred requesting access token from wechat server. Message ={}", response.getErrmsg());
        return null;
    }

    @Override
    public String getToken(String appId) throws ConnectException {
        String token = cacheService.getTokenByAppId(appId);
        if (StrUtil.isEmpty(token)) {
            return refreshAndGetToken(appId);
        }
        return token;
    }

    @Override
    public String refreshAndGetToken(String appId) throws ConnectException {
        Ticket ticket = ticketRepo.findByAppId(appId);
        Objects.requireNonNull(ticket);
        String uri = String.format(WECHAT_TOKEN_URI, appId, ticket.getSecret());
        WechatResponse response = restTemplate.getForEntity(uri, WechatResponse.class).getBody();
        if (response == null) {
            throw new ConnectException("An unexpected error occurred requesting access token from wechat server.");
        }
        if (response.getAccess_token() != null) {
            Date now = new Date();
            ticket.setToken(response.getAccess_token());
            ticket.setExpiredAt(DateUtil.offsetSecond(now, response.getExpires_in()));
            ticket.setLastRefreshedAt(now);
            ticket.setCreatedAt(now);
            ticketRepo.save(ticket);
            cacheService.refreshToken(appId, response.getAccess_token());

            return response.getAccess_token();
        }
        log.error("An error occurred requesting access token from wechat server. Message ={}", response.getErrmsg());
        return null;
    }

    @Override
    public void refreshToken(String appId) throws ConnectException {
        if (StrUtil.isNotEmpty(appId)) {
            refreshAndGetToken(appId);
            return;
        }
        log.info("Found appId is empty.Refreshing all tokens.");
        List<Ticket> tickets = ticketRepo.findAll();
        tickets.forEach(ticket -> {
            try {
                refreshAndGetToken(ticket.getAppId());
            } catch (ConnectException e) {
                e.printStackTrace();
            }
        });
        log.info("All tokens refreshed.");
    }


}
