# zuul调研纪要

## 点

### Hystrix
#### 简介
http://tietang.wang/2016/03/09/hystrix/Hystrix%E7%AE%80%E4%BB%8B/




----

# oss（单点登录）
## oauth
### 简介
http://www.ruanyifeng.com/blog/2014/05/oauth_2_0.html

http://www.jianshu.com/p/0db71eb445c8

## CAS
https://my.oschina.net/eduosi/blog/203885

## CAS、OAuth和OpenID的区别
`CAS`用于站内单点登录。
`OAuth`关注的是authorization；而`OpenID`侧重的是authentication。从表面上看，这两个英文单词很容易混淆，但实际上，它们的含义有本质的区别：

- authorization: n. 授权，认可；批准，委任
- authentication: n. 证明；鉴定；证实

OAuth关注的是授权，即：“用户能做什么”；而OpenID关注的是证明，即：“用户是谁”。

也就是说，`OpenID` 只是告诉网站或别人，这个帐号是你而已，并不会也无法提供其它数据。`OAuth` 之后，就相当于把你微博的数据（比如看私信）和权利（比如发私信）交给了这个网站，至于网站要做什么你根本不知道。