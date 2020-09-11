package com.wechat.token.service;

import cn.hutool.core.date.DateUtil;
import com.wechat.token.model.Ticket;
import com.wechat.token.repo.TicketRepo;
import com.wechat.token.schedule.ScheduleHelper;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;

@Service
public class InitializeService {

    private final TicketRepo ticketRepo;
    private final ScheduleHelper scheduleHelper;

    public InitializeService(TicketRepo ticketRepo, ScheduleHelper scheduleHelper) {
        this.ticketRepo = ticketRepo;
        this.scheduleHelper = scheduleHelper;
    }

    @PostConstruct
    public void bootstrapInitTokens() {
        List<Ticket> tickets = ticketRepo.findAll();
        tickets.forEach(ticket -> {
            Date now = new Date();
            Date nextStartAt;
            Date idealStartAt = DateUtil.offsetSecond(ticket.getLastRefreshedAt(), Math.toIntExact(ticket.getRefreshInterval()));
            if (idealStartAt.before(now)) {
                nextStartAt = now;
            } else {
                nextStartAt = idealStartAt;
            }
            scheduleHelper.alterJob(ticket.getAppId(), nextStartAt, ticket.getRefreshInterval(), ticket.getLastRefreshedAt());
        });
    }
}
