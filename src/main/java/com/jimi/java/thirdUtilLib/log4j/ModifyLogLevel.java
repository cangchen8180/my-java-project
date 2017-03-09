package com.jimi.java.thirdUtilLib.log4j;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Enumeration;

/**
 * Created by lagou on 2017/3/7.
 */
public class ModifyLogLevel {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger("filterLogger");

    public static void main(String[] args) {


        String hostName = "serverHostName";
        //注意：MDC和NDC都是线程独立的
        MDC.put(hostName, "localhost-jimi");
        logger.info("========= info ===============");
        MDC.remove(hostName);

        // ================================================= 动态修改log级别 =========================================================
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
