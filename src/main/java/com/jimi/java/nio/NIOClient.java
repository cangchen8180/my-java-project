package com.jimi.java.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author jimi
 * @description NIO客户端
 * @date 2016-02-03 14:26.
 */
public class NIOClient {
    //通道管理器
    private Selector selector;

    /**
     * 获得一个Socket通道，并对该通道做一些初始化工作
     * @param ip
     * @param port
     * @throws IOException
     */
    public void initClient(String ip, int port) throws IOException {
        //获取一个Socket通道
        SocketChannel channel = SocketChannel.open();
        //设置通道为非阻塞
        channel.configureBlocking(false);
        //获取一个通道管理器
        this.selector = Selector.open();

        //客户端连接服务器，其实方法执行并没有实现连接，需要在listen()方法中
        //调用channel.finishConnect();才能完成连接
        channel.connect(new InetSocketAddress(ip, port));
        //将通道管理器和该通道绑定，并为该通道注册SelectionKey.OP_CONNECT事件
        channel.register(selector, SelectionKey.OP_CONNECT);

    }


    public void listen() throws IOException {
        //轮询访问selector
        while(true){
            selector.select();
            //获取selector中选中的项的迭代器
            Iterator ite = this.selector.selectedKeys().iterator();
            while(ite.hasNext()){
                SelectionKey key = (SelectionKey) ite.next();
                //删除已选的key，以防止重复处理
                ite.remove();
                //连接事件发生
                if(key.isConnectable()){
                    SocketChannel channel = (SocketChannel) key.channel();
                    //如果正在连接，则完成连接
                    if(channel.isConnectionPending()){
                        channel.finishConnect();
                    }
                    //设置成非阻塞
                    channel.configureBlocking(false);

                    //这里可以给服务器端发送信息
                    channel.write(ByteBuffer.wrap(new String("wo is client").getBytes()));

                    //在和服务器连接成功之后，为了可以接收服务端信息，需要给通道设置读的权限
                    channel.register(this.selector, SelectionKey.OP_READ);

                }
                //获得可读事件
                else if(key.isReadable()){
                    read(key);
                }
            }
        }
    }

    /**
     * 客户端读取信息
      * @param key
     * @throws IOException
     */
    public void read(SelectionKey key) throws IOException {
        //获取通道
        SocketChannel channel = (SocketChannel) key.channel();
        //设置通道为非阻塞
        channel.configureBlocking(false);

        //从通道读取数据
        //capacity大小代表什么???
        ByteBuffer inBuffer = ByteBuffer.allocate(1024);
        channel.read(inBuffer);
        System.out.println("客户端接收的信息：" + new String(inBuffer.array()));

        //再给服务器端写信息
        //ByteBuffer outBuffer = ByteBuffer.wrap(new String("客户端收到您的信息").getBytes());
        //channel.write(outBuffer);
    }

    public static void main(String[] args){
        NIOClient client = new NIOClient();
        try {
            client.initClient("127.0.0.1", 8000);
            client.listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
