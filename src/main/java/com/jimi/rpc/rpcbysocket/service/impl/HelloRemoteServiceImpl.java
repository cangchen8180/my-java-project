package com.jimi.rpc.rpcbysocket.service.impl;

import com.jimi.rpc.rpcbysocket.service.api.HelloRemoteService;

/**
 * @author jimi
 * @description
 * @date 2016-01-28 15:47.
 */
public class HelloRemoteServiceImpl implements HelloRemoteService {

    public String hello(String str) {
        System.out.println("[hello] str=" + str);
        return "[hello]" + str;
    }

    public String say(String str) {
        System.out.println("[say] str=" + str);
        return "[say]" + str;
    }
}
