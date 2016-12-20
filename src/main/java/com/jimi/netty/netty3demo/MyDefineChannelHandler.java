package com.jimi.netty.netty3demo;/*

*/
/**
 * @author jimi
 * @description
 * 自定义channel处理handler类
 * SimpleChannelHandler帮我们实现好了很多有用户的方法这里就只重写了几个方法
 * @date 2016-02-17 14:17.
 *//*


import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

class MyDefineChannelHandler extends SimpleChannelHandler {

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        super.channelConnected(ctx, e);
        System.out.println("server channel Connected......");
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        super.messageReceived(ctx, e);
        System.out.println("[接受到的信息]" + e.getMessage());
        // 返回信息可以在dos对话框中看到自己输的内容
        e.getChannel().write(e.getMessage());
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        super.channelClosed(ctx, e);
        System.out.println("channelClosed");
    }
}*/
