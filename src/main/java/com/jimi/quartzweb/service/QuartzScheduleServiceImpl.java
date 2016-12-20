package com.jimi.quartzweb.service;

import com.jimi.quartzweb.quartz.jobs.LGQuartzTestJob;
import com.jimi.quartzweb.quartz.listeners.LGQuartzTestJobListener;
import org.quartz.*;
import org.quartz.impl.matchers.KeyMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

/**
 * @author jimi
 * @description
 * @date 2016-04-08 17:22.
 */
@Service
public class QuartzScheduleServiceImpl implements QuartzScheduleService {

    private static Logger logger = LoggerFactory.getLogger(QuartzScheduleServiceImpl.class);

    private static String jobName = "lg_job_1";
    private static String jobGroup = "lg_job_group";
    private static String triggerName = "lg_trigger_1";

    @Autowired
    SchedulerFactoryBean schedulerFactoryBean;

    @Override
    public void scheduleJob() {
        //获取quartz调度工厂
        Scheduler scheduler = schedulerFactoryBean.getScheduler();

        try {
            //========start============
            scheduler.start();
            JobKey jobKey = new JobKey(jobName, jobGroup);
            boolean exists = scheduler.checkExists(jobKey);
            if (exists){
                logger.info("[Quartz]任务已存在，执行删除操作！");
                boolean deleted = scheduler.deleteJob(jobKey);
                if (deleted){
                    logger.info("[Quartz]任务删除成功");
                }
            }

            //========new job============
            JobDetail jobDetail = JobBuilder.newJob(LGQuartzTestJob.class)
                    .withIdentity(jobName, jobGroup)
                    .build();

            //========new trigger============

            //一个job可以有多个trigger
            /*
            在2.x版本中，这些具体的Trigger类都被废弃了，取而代之的是TriggerBuilder中的withSchedule方法。该方法需要传入一个SechduleBuilder对象，通过该对象来实现触发器的逻辑。
            如示例中所示，2.2版本中的ScheduleBuilder有三种，分为是SimpleScheduleBuilder，CronScheduleBuilder及CalendarIntervalScheduleBuilder：
                1、SimpleScheduleBuilder是简单调用触发器，它只能指定触发的间隔时间和执行次数；
                2、CronScheduleBuilder是类似于Linux Cron的触发器，它通过一个称为CronExpression的规则来指定触发规则，通常是每次触发的具体时间；（关于CronExpression，详见：官方，中文网文）
                3、CalendarIntervalScheduleBuilder是对CronScheduleBuilder的补充，它能指定每隔一段时间触发一次。
            需要注意的是，withSchedule最多可能被同一个TriggerBuilder对象调用一次。若不调用，则任务会立即执行，且只执行一次。
             */

            //========trigger need schedule============
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerName, jobGroup)
                    .startNow()
//                    .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(10).repeatForever())   //简单调度
                    .withSchedule(CronScheduleBuilder.cronSchedule("0/6 * * * * ?"))    //定时调度
                    .build();

            //========add listener for job============
            Matcher<JobKey> jobKeyMatcher = KeyMatcher.keyEquals(jobKey);
            scheduler.getListenerManager().addJobListener(new LGQuartzTestJobListener(), jobKeyMatcher);


            //========schedule job============
            try {
                scheduler.scheduleJob(jobDetail, trigger);
            } catch (SchedulerException e) {
                logger.warn("[Quartz]添加任务异常");
                e.printStackTrace();
            }

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //========reschedule job============
            /*TriggerKey triggerKey = new TriggerKey(triggerName, jobGroup);
            boolean triggerExists = scheduler.checkExists(triggerKey);
            if (triggerExists){
                logger.info("[Quartz]trigger[{}]已存在，执行更新操作", triggerKey.toString());
            }
            Trigger newTrigger = TriggerBuilder.newTrigger()
                    .startNow()
                    .withIdentity(triggerKey)
                    .withSchedule(CronScheduleBuilder.cronSchedule("0/10 * * * * ?"))
                    .build();
            scheduler.rescheduleJob(triggerKey, newTrigger);*/

            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //========shutdown============
            //scheduler.shutdown();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

    }
}
