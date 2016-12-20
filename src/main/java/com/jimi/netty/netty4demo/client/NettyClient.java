package com.jimi.netty.netty4demo.client;

import com.jimi.netty.netty4demo.Constants;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * @author jimi
 * @description
 * @date 2016-02-17 16:26.
 */
public class NettyClient implements Runnable {

    @Override
    public void run() {
        //注意：server端引导对象为ServerBootstrap
        Bootstrap bootstrap = new Bootstrap();

        //创建一个EventLoopGroup来处理各种事件，如处理链接请求，发送接收数据等。
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group);

        //注：在server端此处为NioServerSocketChannel.class
        bootstrap.channel(NioSocketChannel.class);
        //注：此处handler也和server端不一样
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                pipeline.addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
                pipeline.addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));
                pipeline.addLast("handler", new MyClientHandler());
            }
        });

        try {
            //i=100000
            for (int i = 0; i < 10; i++) {
                //发起连接
                ChannelFuture f = bootstrap.connect(Constants.DEFAULT_IP, Constants.DEFAULT_PORT).sync();
                //client端先向server发送信息
                f.channel().writeAndFlush("hello Service!" + Thread.currentThread().getName() + ":--->:" + i);
                f.channel().closeFuture().sync();
                System.out.println("Netty Client channel已关闭");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }

    }


    public static void main(String[] args) throws Exception {
        //i=100
        for (int i = 0; i < 8; i++) {
            new Thread(new NettyClient(),">>>this thread " + i).start();
        }
    }
}
