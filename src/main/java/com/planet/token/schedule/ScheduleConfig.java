package com.planet.token.schedule;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ScheduleConfig {

    @Bean
    Scheduler scheduler() throws SchedulerException {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler=schedulerFactory.getScheduler();
        scheduler.start();
        return scheduler;
    }


}
