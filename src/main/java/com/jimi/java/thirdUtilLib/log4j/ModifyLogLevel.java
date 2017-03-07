package com.jimi.java.thirdUtilLib.log4j;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Enumeration;

/**
 * Created by lagou on 2017/3/7.
 */
public class ModifyLogLevel {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger("filterLogger");

    public static void main(String[] args) {

        logger.info("========= info ===============");
        logger.debug("========= debug ===============");
/*
        动态修改日志级别
         */
        //获取logger框架中当前日志实例
        Enumeration currentLoggers = LogManager.getCurrentLoggers();
        while (currentLoggers.hasMoreElements()) {

            //对root logger做特殊处理
            //if ("ROOT".equals(loggerName)) return LogManager.getRootLogger();

            Logger logger = (Logger) currentLoggers.nextElement();

            Level level = logger.getLevel();
            if (null != level) {
                String levelStr = level.toString();
                System.out.println(logger.getName() + ", level = " + levelStr);

                //动态修改日志级别
                if ("INFO".equalsIgnoreCase(levelStr)) {
                    logger.setLevel(Level.DEBUG);
                }

                //修改完，立马起作用
                logger.debug(logger.getName() + "========= debug ===============");
            }
        }
    }
}
