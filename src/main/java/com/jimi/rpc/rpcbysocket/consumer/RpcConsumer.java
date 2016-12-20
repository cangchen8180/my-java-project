package com.jimi.rpc.rpcbysocket.consumer;

import com.jimi.rpc.rpcbysocket.Contants;
import com.jimi.rpc.rpcbysocket.framework.RpcFramework;
import com.jimi.rpc.rpcbysocket.service.api.HelloRemoteService;

/**
 * @author jimi
 * @description
 * @date 2016-01-28 15:43.
 */
public class RpcConsumer {

    public static void main(String[] args){
        HelloRemoteService helloRemoteService = RpcFramework.refer(HelloRemoteService.class, Contants.RPC_SERVER_IP, Contants.RPC_SERVER_PORT);
        /*String result = helloRemoteService.hello("hi provider, i am method hello" + 0);
        System.out.println("result=" + result);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        String sayResult = helloRemoteService.say("hi provider, i am method say");
        System.out.println("sayResult=" + sayResult);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
