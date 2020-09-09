package com.planet.token.schedule;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

@Slf4j
public class RefreshTokenJob implements Job {


    @Override
    public void execute(JobExecutionContext context) {
        log.info("Now executing scheduled task.");
        JobDataMap dataMap = context.getMergedJobDataMap();
        try {
            ((BeanJob) dataMap.get("beanJob")).executeBeanJob(dataMap.getString("APP_ID"));
        } catch (Exception e) {
            e.printStackTrace();
        }

//            tokenService.refreshToken(context.getMergedJobDataMap().getString("APP_ID"));
    }

}
