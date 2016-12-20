package com.jimi.netty.netty3demo;/*

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

*/
/**
 * @author jimi
 * @description
 * @date 2016-02-17 14:00.
 *//*

public class NettyServer {
    private ServerBootstrap serverBootstrap;
    private int DEFAULT_PORT = 9191;

    */
/**
     * 创建对象时就启动server，默认端口9191
     *//*

    public NettyServer(){
        //初始化
        this._init();
    }
    */
/**
     * 创建对象时就启动server，指定端口
     *//*

    public NettyServer(int port){
        //初始化
        this._init(port);
    }

    private void _init(){
        int port = this.getDefaultPort();
        this._init(port);
    }

    private void _init(int port){
        if(port <= 1024){
            port = this.getDefaultPort();
        }
        this._doInit(port);
    }

    private void _doInit(int port){
        //初始化netty Server
        serverBootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
        */
/**
         * 事实上用户不需要创建pipeline，因为使用ServerBootstrap或Bootstrap启动服务端或客户端时，
         * Netty会为每个Channel创建一个独立的pipeline,对于使用者而言，只需要将自定义的拦截器加入到pipeline即可
         *//*

        ChannelPipeline channelPipeline = serverBootstrap.getPipeline();
        //对经过的字符串格式信息配置过滤器处理
        */
/**
         * 注：此处channelPipeline内部管理拦截器的结构是链表，所以此名addLast的name参数可以随意取，
         * 消息经过时会 顺存 使用添加的这些拦截器去处理。
         *//*

        channelPipeline.addLast("encode", new StringEncoder());
        //对接受的信息解码
        channelPipeline.addLast("decode", new StringDecoder());
        */
/**
         * 将自定义的处理类(拦截器)加入到pipeline
         *//*

        MyDefineChannelHandler myDefineChannelHandler = new MyDefineChannelHandler();
        channelPipeline.addLast("myDefineChannelHandler", myDefineChannelHandler);

        //绑定端口，启动netty server端
        serverBootstrap.bind(new InetSocketAddress(port));
    }

    */
/**
     * 获得默认端口，protected表示该方法支持本包和外包其子类调用。
     *//*

    protected int getDefaultPort(){
        return DEFAULT_PORT;
    }



    */
/**
     * 启动nettyServer
     * 然后在终端输入telnet 127.0.0.1 8080
     * 再输入内容
     * 观察console打印的内容
     * @param args
     *//*

    public static void main(String[] args){
        new NettyServer();
    }

}

*/
