## spring mvc启动过程

### 一、概述

下面一个基本的运用springMVC的的web.xml的配置，这里要注意两个地方，一个是ContextLoadListener，一个是DispatcherServlet。web容器正是通过这两个配置才和Spring关联起来。这两个配置与web容器的ServletContext关联，为Spring的Ioc容器提供了一个宿主，在建立起Ioc容器体系之后，把DispatcherServlet作为Spring MVC处理web请求的转发器建立起来，从而完成响应Http请求的准备。

Spring MVC启动过程依据这两个配置大致分为两个过程：

1. ContextLoaderListener初始化，实例化IoC容器，并将此容器实例注册到ServletContext中。
2. DispatcherServlet初始化，建立自己的上下文，也注册到ServletContext中。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app id="WebApp_ID" version="2.4"
 xmlns="http://java.sun.com/xml/ns/j2ee"
 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 xsi:schemaLocation="http://java.sun.com/xml/ns/j2eehttp://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd">
<listener>
　　<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>
<context-param>
　　<param-name>contextConfigLocation</param-name>
　　<param-value>/WEB-INF/applicationContext.xml,/WEB-INF/controllers.xml,/WEB-INF/service.xml</param-value>
</context-param>
<servlet>
　　<servlet-name>dispatch</servlet>
　　<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
　　<init-param>
　　　　<param-name>contextConfigLocation</param-name>
　　　　<param-value>/WEB-INF/applicationContext.xml</param-value>
　　</init-param>
</servlet>
<servlet-mapping>
　　<servlet-name>dispatch</servlet-name>
　　<servlet-pattern>*.*</servlet-pattern>
</servlet-mapping>
</web-app>
```

### 二、Spring IOC容器的启动
ContextLoaderListener实现ServletContextListener，这个接口里面的函数会结合web容器的生命周期被调用。因为ServletContextListener是ServletContext的监听者，如果ServletContext发生变化，会触发相应的事件，而监听者一直对这些事件进行监听，如果接受到了监听的事件，就会作出预先设计好的动作。例如在服务器启动，ServletContext被创建的时候，ServletContextListener的contextInitialized()方法被调用，从而拉开了初始化Spring IOC容器的大幕。

![](https://github.com/cangchen8180/my-java-project/blob/f94245ed85244f80a45c2bec4d35172567be2575/src/main/java/com/jimi/java/_interview/springmvc/imgs/345531-20151122230034686-637075708.png)

首先从Servlet的启动事件中得到ServletContext，然后读取web.xml中的各个相关的属性值，接着ContextLoader会实例化WebApplicationContext，并完成载入和初始化的过程，这个被初始化的第一个上下文作为根上下文而存在，这个根上下文载入后，被绑定到web应用程序的ServletContext上，这样，IOC容器中的类就可以在任何地方访问到了。

具体的Ioc容器的载入过程在refresh()中实现，这个方法主要干了以下几件事情：

1. Resource文件的定位，即找到bean的配置文件
2. 通过特定的reader解析该bean配置文件，抽象成beanDefinition类
3. 将beanDefinition向容器注册，写入到一个大的HashMap中，供实例化请求的时候使用

### 三、DispatchServlet的启动
DispatchServlet本质上是一个Servlet，web容器启动的时候，servlet也会初始化，其init方法被调用，开启初始化之旅。

DispatchServlet会建立自己的上下文来持有Spring MVC特殊的bean对象，在建立这个自己持有的Ioc容器的时候，会从ServletContext中得到根上下文作为DispatchServlet上下文的parent上下文。有了这个根上下文，再对自己持有的上下文进行初始化，最后把自己持有的这个上下文保存到ServletContext中，供以后检索和使用。

![](https://github.com/cangchen8180/my-java-project/blob/f94245ed85244f80a45c2bec4d35172567be2575/src/main/java/com/jimi/java/_interview/springmvc/imgs/345531-20151122232537843-1405494113.png)

在initWebApplicationContext中完成了对自己上下文的初始化，这里面也有一个refresh的过程，和普通的Ioc容器初始化大同小异。

另外一些MVC的特性初始化时在initStrategies()中实现的，包括支持国际化的LocalResolver、支持Request映射的HandlerMappings，以及视图生成的ViewResolver等等。

### 四、DispatcherServlet的分发处理Http请求
初始化完成后，上下文环境中已经定义的所有HandlerMapping都已经被加载了，这些被加载的HandlerMapping放在List中，存储着Http请求和Controller的映射数据。

DispatcherServlet是HttpServlet的子类，也是通过doService()来响应HTTP请求的，doService()中采用doDispatch()方法实现的。

![](https://github.com/cangchen8180/my-java-project/blob/f94245ed85244f80a45c2bec4d35172567be2575/src/main/java/com/jimi/java/_interview/springmvc/imgs/345531-20151122234250983-1116362564.png)

1. 首先通过url匹配，通过getHandler找到Handler，Hander中封装了配置的Controller
2. 执行Handler，得到返回的ModelAndView结果
3. 最后把这个ModelAndView对象交给视图对象的render()方法去呈现。

转载[Spring MVC的启动过程](http://www.cnblogs.com/mingziday/p/4987058.html)

