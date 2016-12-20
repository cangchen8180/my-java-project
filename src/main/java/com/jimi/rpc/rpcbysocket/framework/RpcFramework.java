package com.jimi.rpc.rpcbysocket.framework;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author jimi
 * @description
 * @date 2016-01-28 15:45.
 */
public class RpcFramework {

    /**
     * 用于提供者提供RPC服务
     * @param service   服务名
     * @param port  端口
     */
    public static void run(final Object service, int port) throws IOException {
        if(service == null){
            throw new IllegalArgumentException("Service is null");
        }
        if(port <= 0 || port > 65535){
            throw new IllegalArgumentException("Invalid port = " + port);
        }

        System.out.println("[RpcFramework.run]Service=" + service.getClass().getName() + ", port=" + port);

        //创建serverSocket
        ServerSocket serverSocket = new ServerSocket(port);
        for(;;){
            System.out.println("service server init success...");
            //接受连接请求，收到后新建线程去处理
            //注：该方法是阻塞式
            final Socket socket = serverSocket.accept();
            new Thread(new Runnable() {
                public void run() {
                    try {
                        System.out.println("socket request...socket=" + socket.toString());
                        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

                        //获取方法名
                        String methodName = input.readUTF();
                        System.out.println("methodName=" + methodName);

                        //获取方法的参数信息
                        Class<?>[] paramTypes = (Class<?>[])input.readObject();
                        System.out.println("paramTypes=" + paramTypes.toString());

                        //获取调用该方法的参数信息
                        Object[] paramValues = (Object[])input.readObject();
                        System.out.println("paramValues=" + paramValues.toString());

                        //输出对象
                        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

                        try {
                            Method method = service.getClass().getMethod(methodName, paramTypes);
                            //动态调用执行类的方法
                            Object result = method.invoke(service, paramValues);
                            System.out.println("[RpcFramework.run]result=" + result.toString());
                            output.writeObject(result);
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }finally {
                            //关闭输入输出
                            input.close();
                            output.close();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }finally {
                        //关闭当前socket
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }

    }

    /**
     * <T>表示此为泛型方法
     * @param interfaceClass
     * @param host
     * @param port
     * @param <T>
     * @return
     */
    public static <T>T refer(final Class<T> interfaceClass, final String host, final int port){
        if(interfaceClass == null){
            throw new IllegalArgumentException("interfaceClass is null");
        }
        if(!interfaceClass.isInterface()){
            throw new IllegalArgumentException("interfaceClass must be interface class");
        }

        if(host == null || host.length() <= 0){
            throw new IllegalArgumentException("host is null");
        }

        if(port <= 0 || port > 65535){
            throw new IllegalArgumentException("Invalid port = " + port);
        }

        System.out.println("[RpcFramework.refer]Get remote service " + interfaceClass.getName() + " from server " + host + ":" + port);

        T t = (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new InvocationHandler() {
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                Socket socket = null;
                try {
                    System.out.println("[RpcFramework.refer]new socket...");
                    socket = new Socket(host, port);
                    System.out.println("[RpcFramework.refer]socket=" + socket.toString());

                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

                    try {
                        //todo：为什么会默认先调用toString()方法？？？
                        if(!method.getName().equals("toString")) {
                            //写入方法名
                            output.writeUTF(method.getName());
                            System.out.println("[RpcFramework.refer]methodName=" + method.getName());
                            //写入方法参数类型
                            output.writeObject(method.getParameterTypes());
                            //写入方法参数值
                            output.writeObject(objects);
                            System.out.println("[RpcFramework.refer]param=" + objects.toString());

                            //input一定要放在output数据之后
                            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                            //等待接受处理的结果
                            Object result = input.readObject();
                            input.close();

                            if (result instanceof Throwable) {
                                throw (Throwable) result;
                            }

                            return result;
                        }

                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }finally {
                        output.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        });

        return t;
    }
}
