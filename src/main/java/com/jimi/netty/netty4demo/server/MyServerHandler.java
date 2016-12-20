package com.jimi.netty.netty4demo.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author jimi
 * @description
 * @date 2016-02-17 16:01.
 */
public class MyServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("channelActive>>>>>>>>");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        //接收到client发过来的信息，并发出相应信息
        System.out.println("server receive message :" + msg);
        ctx.channel().writeAndFlush("yes server already accept your message" + msg);
        ctx.close();//server端作出相应之后就关闭当前channel
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        System.out.println("exception is general");
        ctx.close();//出现异常时关闭channel
    }
}
