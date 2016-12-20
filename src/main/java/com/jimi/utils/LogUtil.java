package com.jimi.utils;

/**
 * @author jimi
 * @description
 * @date 2016-04-09 13:44.
 */
public class LogUtil {

    /**
     * exception log信息
     */
    public static String excpetionLogStyle(String[] prefixs, String... messages){
        StringBuilder log = new StringBuilder();
        log.append("[log]");

        //前缀
        if (null != prefixs && prefixs.length > 0){
            for (int i = 0; i < prefixs.length; i++) {
                log.append("[").append(prefixs[i]).append("]");
            }
        }

        //message
        if (null != messages && messages.length > 0){
            for (int i = 0; i < messages.length; i++) {
                log.append(messages[i]).append("; ");
            }
        }

        return log.toString();
    }
}
