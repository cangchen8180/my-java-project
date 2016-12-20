package com.jimi.rpc.rpcbysocket.provider;

import com.jimi.rpc.rpcbysocket.Contants;
import com.jimi.rpc.rpcbysocket.framework.RpcFramework;
import com.jimi.rpc.rpcbysocket.service.api.HelloRemoteService;
import com.jimi.rpc.rpcbysocket.service.impl.HelloRemoteServiceImpl;

import java.io.IOException;

/**
 * @author jimi
 * @description
 * @date 2016-01-28 15:39.
 */
public class RpcProvider {

    public static void main(String[] args){
        //提供者就是将服务实现提供给客户端
        HelloRemoteService helloRemoteService = new HelloRemoteServiceImpl();
        try {
            RpcFramework.run(helloRemoteService, Contants.RPC_SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
