package com.wechat.token.schedule;

import com.wechat.token.repo.TicketRepo;
import com.wechat.token.service.TokenService;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ScheduleHelper {

    private final Scheduler scheduler;
    private final TokenService tokenService;
    private final TicketRepo ticketRepo;

    public ScheduleHelper(Scheduler scheduler, TokenService tokenService, TicketRepo ticketRepo) {
        this.scheduler = scheduler;
        this.tokenService = tokenService;
        this.ticketRepo = ticketRepo;
    }

    public void alterJob(String appId, Date startAt, Long interval, Date lastRefreshAt) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("APP_ID", appId);
        jobDataMap.put("beanJob", new RefreshTokenActJob(tokenService));
        if (interval == null || interval == 0L) {
            interval = ticketRepo.findByAppId(appId).getRefreshInterval();
        }
        String jobKey = "REFRESH_TOKEN_JOB_:" + appId;
        String triggerKey = "TRIGER_" + appId;
        JobDetail jobDetail = JobBuilder.newJob(RefreshTokenJob.class)
                .setJobData(jobDataMap).withIdentity(jobKey).build();
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey, "TOKEN_REFRESH_GROUP").startAt(startAt)
                .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(Math.toIntExact(interval)))
                .build();

        try {
            if (scheduler.getTriggerKeys(GroupMatcher.anyGroup()).contains(TriggerKey.triggerKey(triggerKey)) || scheduler.getJobKeys(GroupMatcher.anyGroup()).contains(JobKey.jobKey(jobKey))) {
                scheduler.rescheduleJob(TriggerKey.triggerKey(triggerKey), trigger);
                return;
            }
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

    }
}
