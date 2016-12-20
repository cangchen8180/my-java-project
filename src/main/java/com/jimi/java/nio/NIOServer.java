package com.jimi.java.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author jimi
 * @description NIO服务器端
 *
 *  ServerSocketChannel主要用在Server中，用于接收客户端的链接请求
 *  SocketChannel则用于真正的读写数据，同时还可以用于客户端发送链接请求。
 *  真正实现读写数据操作的就是这些SocketChannel，上面的ServerSocketChannel只是负责接收连接请求。
 *
 * 注：1、Selector和Channel关系
 * <p>(1)在channel注册到selector时，如<code>serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);</code>
 *    会将当前channel、selector和欲监听的事件状态绑定生成一个SelectionKey对象，并将这个对象放在该selector维护的SelectionKey对象集合中。
 *
 *    当等待到就绪事件时，从相应SelectionKey对象中取出channel，并做通信和数据处理。</p>
 * <p>(2)channel可以注册到一个或多个Selector上以进行异步IO操作。</p>
 * @date 2016-02-03 13:49.
 */
public class NIOServer {
    //通道管理器
    private Selector selector;

    /**
     * 获取一个ServerSocket通道，并对该通道做一些初始化工作
     * @param port 绑定的端口
     * @throws IOException
     */
    public void initServer(int port) throws IOException {
        //获取一个ServerSocket通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //设置通道为非阻塞
        serverSocketChannel.configureBlocking(false);
        //将该通道对应的ServerSocket绑定到port端口
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        //获取一个通道管理器
        this.selector = Selector.open();
        //将通道管理器和该通道绑定，并为该通道注册SelectionKey.OP_ACCEPT事件，
        //注册该事件后，当该事件到达时，selector.select()会返回;如果该事件未到达，selector.select()会一直阻塞。
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

    }

    /**
     * 采用轮询的方式监听selector上是否有需要处理的事件，若有，则处理
     */
    public void listen() throws IOException {
        System.out.println("服务端启动成功！");
        //轮询访问selector
        while(true){
            //当注册事件到达时，方法返回;否则，该方法会一直阻塞。
            selector.select();
            //获取selector中选中的项的迭代器，选中的项为注册事件。
            Iterator ite = this.selector.selectedKeys().iterator();
            while(ite.hasNext()){
                SelectionKey key = (SelectionKey) ite.next();
                //删除已选的key，以防止重复处理
                ite.remove();
                //客户端请求连接事件
                if(key.isAcceptable()){
                    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                    //获得和客户端连接的通道
                    SocketChannel channel = serverSocketChannel.accept();
                    //设置为非阻塞
                    channel.configureBlocking(false);

                    //在这里可以给客户端发送信息
                    channel.write(ByteBuffer.wrap(new String("hi client, i am server").getBytes()));

                    //在和客户端连接成功后，为了可以接收客户端信息，需要给通道设置读的权限
                    channel.register(this.selector, SelectionKey.OP_READ);
                }
                //设置可读的事件
                else if(key.isReadable()){
                    read(key);
                }


            }
        }
    }

    /**
     * 处理读取客户端发来的信息的事件
     * @param key
     * @throws IOException
     */
    public void read(SelectionKey key) throws IOException {
        //服务器可读取消息：得到事件发生的Socket通道
        SocketChannel channel = (SocketChannel) key.channel();
        //创建读取的缓冲区
        //注：此处设置capacity的大小和接受信息的数量有关
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        channel.read(buffer);
        byte[] data = buffer.array();
        String msg = new String(data).trim();
        System.out.println("服务端收到消息：" + msg);

        //将消息再推回客户端
        //ByteBuffer outBuffer = ByteBuffer.wrap(msg.getBytes());
        //channel.write(outBuffer);

    }

    public static void main(String[] args){
        //启动server
        NIOServer server = new NIOServer();
        try {
            server.initServer(8000);
            server.listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
