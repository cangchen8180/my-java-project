## Session的实现原理，与Cookie的关系

由于HTTP是无状态的，浏览器每次访问web页面，都是打开到Server的独立连接，并且不维护客户的上下文信息。

如果需要维护上下文信息，比如用户登录后的信息。有三种实现方式：Cookie、url重写和隐藏表单域。但cookie方式更常用。

### 区别
cookie机制采用的是在客户端保持状态的方案，而session机制采用的是在服务器端保持状态的方案。

- session保存在服务器，客户端不知道其中的信息；cookie保存在客户端，服务器能够知道其中的信息。
- session中保存的是对象，cookie中保存的是字符串。
- session不能区分路径，同一个用户在访问一个网站期间，所有的session在任何一个地方都可以访问到。而cookie中如果设置了路径参数，那么同一个网站中不同路径下的cookie互相是访问不到的。

### Cookie

Cookie是一种服务器和客户端相结合的技术，当客户端第一次（在不新开浏览器的前提下）访问server时，server会生成一个新的sessionId，然后放在响应结果里，客户端保存到本地cookie，后面再访问该server的网页时，server就能从request中读取到该sessionId，从而判断是否是同一个用户。

**请求**  

```data
POST /ibsm/LoginAction.do HTTP/1.1  
Accept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-shockwave-flash, application/vnd.ms-excel, application/vnd .ms-powerpoint, application/msword, */*  
Referer: http://192.168.1.20:8080/crm/  
Accept-Language: zh-cn  
Content-Type: application/x-www-form-urlencoded  
UA-CPU: x86  
Accept-Encoding: gzip, deflate  
User-Agent: Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.2)  
Host: 192.168.1.20:8080  
Content-Length: 13  
Connection: Keep-Alive  
Cache-Control: no-cache  
   
username=jack  
```

**响应**

```data
HTTP/1.1 200 OK  
Server: Apache-Coyote/1.1  
Set-Cookie: JSESSIONID=3267A671BFEAA147A2383B7E083D4G7E; Path=/crm  
Content-Type: text/html;charset=GBK  
Content-Length: 436  
Date: Sat, 10 June 2009 12:43:26 GMT
```

生成响应的时候，服务器向客户端发送cookie。cookie的属性是JSESSIONID，值是267A671BFEAA147A2383B7E083D4G7E。以后每次客户端请求时，都会附上此cookie，服务器端就可以读取到。

```data
GET /ibsm/ApplicationFrame.frame HTTP/1.1  
Accept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-shockwave-flash, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*  
Accept-Language: zh-cn  
UA-CPU: x86  
Accept-Encoding: gzip, deflate  
User-Agent: Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.2)  
Host: 192.168.1.20:8080  
Connection: Keep-Alive  
Cookie: JSESSIONID=267A671BFEAA147A2383B7E083D4G7E 
```

服务器端根据读取到的JSESSIONID，在一个HashMap里面查找其对应的session对象，这个HashMap的 **key是jsessionid的值，value是session对象**。

### Session

#### Session存储
Tomcat内部使用HashMap维护创建的sessionId和session对象信息（key是sessionId的值，value是session对象）,源码中manager.findSession(requestSessionId)用于查找此会话ID对应的session对象。

Session对象的各属性信息的维护、存取也是采用HashMap来实现。Tomcat里面Session实现类是StandardSession，里面一个attributes属性。

```java
/**
 * The collection of user data attributes associated with this Session.
 */
private HashMap attributes = new HashMap();
```

#### Session过期
Session会话信息不会一直在服务器端保存，超过一定的时间期限就会被删除，这个时间期限可以在web.xml中进行设置，不设置的话会有一个默认值，Tomcat的默认值是60。那么服务器端是怎么判断会话过期的呢？原理服务器会启动一个线程，一直查询所有的Session对象，检查不活动的时间是否超过设定值，如果超过就将其删除。

见StandardManager类，它实现了Runnable接口，里面的run方法如下，

```java
/**
 * The background thread that checks for session timeouts and shutdown.
 */
public void run() {

    // Loop until the termination semaphore is set
    while (!threadDone) {
        threadSleep();
        processExpires();
    }

}

/**
 * Invalidate all sessions that have expired.
 */
private void processExpires() {

    long timeNow = System.currentTimeMillis();
    Session sessions[] = findSessions();

    for (int i = 0; i < sessions.length; i++) {
        StandardSession session = (StandardSession) sessions[i];
        if (!session.isValid())
            continue;
        int maxInactiveInterval = session.getMaxInactiveInterval();
        if (maxInactiveInterval < 0)
            continue;
        int timeIdle = // Truncate, do not round up
            (int) ((timeNow - session.getLastUsedTime()) / 1000L);
        if (timeIdle >= maxInactiveInterval) {
            try {
                expiredSessions++;
                session.expire();
            } catch (Throwable t) {
                log(sm.getString("standardManager.expireException"), t);
            }
        }
    }
}
```

### 分布式Session的四种实现方式

#### 1、Cookie方式共享Session
这个方案我们可能比较陌生，但它在大型网站中还是比较普遍被使用。原理是将全站用户的Session信息加密、序列化后以Cookie的方式，统一种植在根域名下（如：.host.com），利用浏览器访问该根域名下的所有二级域名站点时，会传递与之域名对应的所有Cookie内容的特性，从而实现用户的Cookie化Session 在多服务间的共享访问。

**优点：**
不需要额外服务器资源。

**缺点：**

- 受http协议头信息长度的限制，仅能够存储小部分的用户信息。
- Cookie化的Session内容需要进行安全加解密（如：采用DES、RSA等进行明文加解密；再由MD5、SHA-1等算法进行防伪认证）。
- 它也会占用一定的带宽资源，因为浏览器会在请求当前域名下任何资源时将本地Cookie附加在http头中传递到服务器。

#### 2、Sticky（粘性）方式共享Session
这种方案是当用户访问集群中某台机器后，强制指定后续所有请求均落到此机器上。

**优点：**
实现简单、配置方便、没有额外网络开销。

**缺点：**
网络中有机器Down掉，用户Session会丢失，容易造成单点故障。

#### 3、Tomcat集群Session复制方式
这种方式基于IP组播(multicast)来完成的。

简单的说，就是需要进行集群的tomcat通过配置 **统一的组播IP和端口来确定一个集群组**, 当一个node的session发生变更的时候, 它会向IP组播发送变更的数据, IP组播会将数据分发给所有组里的其他成员(node)。

**优点：**
实现简单、配置较少、当网络中有机器Down掉时不影响用户访问。

**缺点：**
组播式复制到其余机器有一定延时，带来一定网络开销。

配置与测试，参考[这里](http://nanquan.iteye.com/blog/1533906)。

#### 4、Redis集群方式共享Session
将Session存入分布式redis集群中，当用户访问不同节点时先从缓存中拿Session信息。

如图，
![redis实现分布式session](https://github.com/cangchen8180/my-java-project/blob/master/src/main/java/com/jimi/java/_interview/web/2587879-b650ae9ea2d8574c.png)

**优点：**

- Session可以持久化，不会丢失用户登录数据。
- 可靠性高，适合复杂网络环境。

**缺点：**
稳定性依赖于缓存的稳定性。

详细实现，参考[这里](http://www.cnblogs.com/lengfo/p/4260363.html)






