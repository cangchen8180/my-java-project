package com.jimi.quartzweb.quartz.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jimi
 * @description
 * @date 2016-04-08 17:54.
 */
public class LGQuartzTestJob implements Job {

    private static Logger logger = LoggerFactory.getLogger(LGQuartzTestJob.class);
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("LGQuartzJob execute...");
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(1000);
                logger.info("[{}]ms[{}].", System.currentTimeMillis(), i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
