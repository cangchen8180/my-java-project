package com.jimi.netty.netty4demo.server;

import com.jimi.netty.netty4demo.Constants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * @author jimi
 * @description
 * @date 2016-02-17 15:44.
 */
public class NettyServer {

    //组线程数-可用处理器的2倍
    private static final int BIZ_GROUP_SIZE = Runtime.getRuntime().availableProcessors() * 2;
    private static final int BIZ_THREAD_SIZE = 100; //线程数

    private static final EventLoopGroup bossGroup = new NioEventLoopGroup(BIZ_GROUP_SIZE);
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup(BIZ_THREAD_SIZE);

    public static void start() throws InterruptedException {
        // 引导辅助程序
        //注：client端引导程序对象为Bootstrap
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        // 通过nio方式来接收连接和处理连接
        //创建一个EventLoopGroup来处理各种事件，如处理链接请求，发送接收数据等。
        serverBootstrap.group(bossGroup, workerGroup);
        // 设置nio类型的channel
        //注：在server端此处为NioSocketChannel.class
        serverBootstrap.channel(NioServerSocketChannel.class);

        //有连接到达时会创建一个channel
        serverBootstrap.childHandler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline channelPipeline = channel.pipeline();
                channelPipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                channelPipeline.addLast(new LengthFieldPrepender(4));
                channelPipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                channelPipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                // pipeline管理channel中的Handler，在channel队列中添加一个handler来处理业务
                channelPipeline.addLast(new MyServerHandler());
            }
        });

        // 设置监听端口，并开始绑定server，通过调用sync同步方法阻塞直到绑定成功
        ChannelFuture f = serverBootstrap.bind(Constants.DEFAULT_IP, Constants.DEFAULT_PORT).sync();
        System.out.println("Netty服务器已启动");

        // 应用程序会一直等待，直到channel关闭
        f.channel().closeFuture().sync();
        System.out.println("Netty服务器channel已关闭");
    }

    /**
     * 关闭EventLoopGroup，释放掉所有资源包括创建的线程
     */
    protected static void shutdown() {
        //关闭EventLoopGroup，释放掉所有资源包括创建的线程
        workerGroup.shutdownGracefully();
        //关闭EventLoopGroup，释放掉所有资源包括创建的线程
        bossGroup.shutdownGracefully();
    }



    public static void main(String[] args){
        System.out.println("开始启动Netty服务器...");
        try {
            NettyServer.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
            //关闭EventLoopGroup
            NettyServer.shutdown();
        }
    }

}
