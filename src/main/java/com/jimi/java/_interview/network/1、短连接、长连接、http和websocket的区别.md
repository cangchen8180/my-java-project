## 短连接、长连接、Http和WebSocket的区别

各层协议如下图：
![](https://github.com/cangchen8180/my-java-project/blob/master/src/main/java/com/jimi/java/_interview/network/9622484_1351732533Vvlb.gif)

### 1、Http、Tcp、Udp和Socket的区别？
答：由上图所示，

- Http：为应用层协议，它是TCP/IP的应用层协议。
- Socket：为应用层和TCP/IP通信的中间软件抽象层，是一组接口，用于把复杂的TCP/IP协议族隐藏在Socket接口的后面。
- Tcp/Udp：为运输层协议
- IP：为网络层协议

### 2、TCP长连接和短连接的区别？
答：TCP连接是指在Client和Server之间传输数据之前，需要先建立一个连接，传输完再释放掉连接。建立连接需要3次握手，释放连接需要4次握手。

而其中，TCP短连接和长连接区别如下，

- TCP短连接
    >建立连接->传输数据->关闭连接
    
    HTTP是无状态的，浏览器和服务器每进行一次HTTP操作，就建立一次连接，但任务结束就中断连接。
    也可以这样说：短连接是指SOCKET连接后发送后接收完数据后马上断开连接。

- TCP长连接
    >建立连接->传输数据->保持连接->传输数据->...->关闭连接
    
    client向server发起连接，server接受client连接，双方建立连接。Client与server完成一次读写之后，它们之间的连接并不会主动关闭，后续的读写操作会继续使用这个连接。
    HTTP的keep-alive的长连接，就是在一次连接中，多次发http请求来实现，但每次都要带着header。

### 3、Http中keep-alive、long poll和WebSocket中长连接的区别？
答：
#### 前两种方式区别

- keep-alive：是指在一次 TCP 连接中完成多个 HTTP 请求，但是对每个请求仍然要单独发 header。
- long poll：类似轮询，是指从客户端（一般就是浏览器）不断主动的向服务器发 HTTP 请求查询是否有新数据。

这两种模式有一个共同的缺点，就是除了真正的数据部分外，服务器和客户端还要大量交换 HTTP header，信息交换效率很低，而且浪费带宽。
而且，http协议决定了浏览器端总是主动发起方，http的服务端总是被动的接受、响应请求，从不主动。

#### WebSocket 
它是 HTML5 一种新的协议。它实现了浏览器与服务器全双工通信，能更好的节省服务器资源和带宽并达到实时通讯，它建立在TCP之上，同HTTP一样通过TCP来传输数据。
但是它和 HTTP 最大不同是：

- WebSocket 是一种双向通信协议，在建立连接后，WebSocket 服务器和 Browser/Client Agent 都能主动的向对方发送或接收数据，就 **像 Socket 建立连接后一直相互通信**一样；
- WebSocket 需要http1.1的方式建立连接，连接成功后才能相互通信。