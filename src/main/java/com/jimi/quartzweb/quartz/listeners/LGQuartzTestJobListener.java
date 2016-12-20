package com.jimi.quartzweb.quartz.listeners;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jimi
 * @description
 * @date 2016-04-08 20:55.
 */
public class LGQuartzTestJobListener implements JobListener {

    private static Logger logger = LoggerFactory.getLogger(LGQuartzTestJobListener.class);

    @Override
    public String getName() {
        return LGQuartzTestJobListener.class.getName();
    }

    /**
     * 这个方法正常情况下不执行,但是如果当TriggerListener中的vetoJobExecution方法返回true时,那么执行这个方法.
     * 需要注意的是 如果方法(2)执行 那么(1),(3)这个俩个方法不会执行,因为任务被终止了嘛
     * @param jobExecutionContext
     */
    @Override
    public void jobToBeExecuted(JobExecutionContext jobExecutionContext) {
        logger.info("任务被终止...");
    }

    /**
     * 任务执行时执行
     * @param jobExecutionContext
     */
    @Override
    public void jobExecutionVetoed(JobExecutionContext jobExecutionContext) {
        logger.info("任务进行中...");
    }

    /**
     * 任务执行后执行
     * @param jobExecutionContext
     * @param e
     */
    @Override
    public void jobWasExecuted(JobExecutionContext jobExecutionContext, JobExecutionException e) {
        logger.info("任务已结束...");
    }
}
