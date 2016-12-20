package com.jimi.rpc.rpcbysocket.service.api;

/**
 * @author jimi
 * @description 远程服务
 * @date 2016-01-28 15:46.
 */
public interface HelloRemoteService {

    /**
     * 打招呼
     * @param str
     */
    public String hello(String str);

    /**
     * 说两句
     * @param str
     * @return
     */
    public String say(String str);
}
