## spring mvc如何管理url和controller的映射

### 加载url和controller映射
#### 如何加载Controller类和方法上的@RequestMapping的url？
Spring MVC使用HandlerMapping接口抽象表示通过请求获取Controller的行为，在使用注解驱动的Spring MVC中，HandlerMapping的具体实现类为：DefaultAnnotationHandlerMapping，该类继承自 AbstractDetectingHandlerMapping，在AbstractDetectingHandlerMapping类中，定义了方法 detectHandlers()，这个方法的目的在于取得所有可能的Controller，并将URL与Controller的映射关系注册到 handlerMap中。

AbstractDetectingHandlerMapping#detectHandlers()源码如下，

```java
//Register all handlers found in the current ApplicationContext.  
protected void detectHandlers() throws BeansException {  
    if (logger.isDebugEnabled()) {  
        logger.debug("Looking for URL mappings in application context: " + getApplicationContext());  
    }  

    //取得容器中的搜有bean  
    String[] beanNames = (this.detectHandlersInAncestorContexts ?  
            BeanFactoryUtils.beanNamesForTypeIncludingAncestors(getApplicationContext(), Object.class) :  
            getApplicationContext().getBeanNamesForType(Object.class));  

    // Take any bean name that we can determine URLs for.  
    for (String beanName : beanNames) {  
        //取得每个bean可以处理的url  
        String[] urls = determineUrlsForHandler(beanName);  
        if (!ObjectUtils.isEmpty(urls)) {  
            // URL paths found: Let's consider it a handler.  
            //注册，将url与controller的映射关系注册到handlerMap中  
            registerHandler(urls, beanName);  
        }  
        else {  
            if (logger.isDebugEnabled()) {  
                logger.debug("Rejected bean name '" + beanName + "': no URL paths identified");  
            }  
        }  
    }  
}  
```    

在AbstractDetectingHandlerMapping中，determineUrlsForHandler(String beanName)是一个抽象方法，由具体的子类给出实现，这里我们需要关注的是DefaultAnnotationHandlerMapping类是如何实现该方法的。
DefaultAnnotationHandlerMapping#determineUrlsForHandler()源码如下，

```java
protected String[] determineUrlsForHandler(String beanName) {  
    ApplicationContext context = getApplicationContext();  
    Class<?> handlerType = context.getType(beanName);  
            //取得该bean类级别的RequestMapping注解  
    RequestMapping mapping = context.findAnnotationOnBean(beanName, RequestMapping.class);  
    if (mapping != null) {  
        // @RequestMapping found at type level  
        this.cachedMappings.put(handlerType, mapping);  
        Set<String> urls = new LinkedHashSet<String>();  
        String[] typeLevelPatterns = mapping.value();  
        if (typeLevelPatterns.length > 0) {  
            // @RequestMapping specifies paths at type level  
            //获取方法中RequestMapping中定义的URL。（RequestMapping可以定义在类上，也可以定义在方法上）  
            String[] methodLevelPatterns = determineUrlsForHandlerMethods(handlerType);  
            for (String typeLevelPattern : typeLevelPatterns) {  
                if (!typeLevelPattern.startsWith("/")) {  
                    typeLevelPattern = "/" + typeLevelPattern;  
                }  
                
                //将类级别定义的URL与方法级别定义的URL合并（合并规则后面再详解），合并后添加到该bean可以处理的URL集合中  
                for (String methodLevelPattern : methodLevelPatterns) {  
                    String combinedPattern = getPathMatcher().combine(typeLevelPattern, methodLevelPattern);  
                    addUrlsForPath(urls, combinedPattern);  
                }  

                //将类级别定义的URL添加到该bean可以处理的URL集合中  
                addUrlsForPath(urls, typeLevelPattern);  
            }  
            return StringUtils.toStringArray(urls);  
        }  
        else {  
            // actual paths specified by @RequestMapping at method level  
            //如果类级别的RequestMapping没有指定URL，则返回方法中RequestMapping定义的URL  
            return determineUrlsForHandlerMethods(handlerType);  
        }  
    }  
    else if (AnnotationUtils.findAnnotation(handlerType, Controller.class) != null) {  
        // @RequestMapping to be introspected at method level  
        //如果类级别没有定义RequestMapping，但是定义了Controller注解，将返回方法中RequestMapping定义的URL     
        return determineUrlsForHandlerMethods(handlerType);  
    }  
    else {  
        //类级别即没有定义RequestMapping，也没有定义Controller，则返回null  
        return null;  
    }  
}  
```

上述代码是Spring处理类级别的RequestMapping注解，但是RequestMapping注解也可以定义在方法级别上，determineUrlsForHandlerMethods()方法是获取该类中定义了RequestMapping注解的方法能够处理的所有 URL。
DefaultAnnotationHandlerMapping#determineUrlsForHandlerMethods()源码如下，

```java
protected String[] determineUrlsForHandlerMethods(Class<?> handlerType) {  
    final Set<String> urls = new LinkedHashSet<String>();  
    //类型有可能是代理类，如果是代理类，则取得它的所有接口  
    Class<?>[] handlerTypes =  
            Proxy.isProxyClass(handlerType) ? handlerType.getInterfaces() : new Class<?>[]{handlerType};  
    for (Class<?> currentHandlerType : handlerTypes){  
        //依次处理该类的所有方法  
        ReflectionUtils.doWithMethods(currentHandlerType, new ReflectionUtils.MethodCallback() {  
            public void doWith(Method method) {  
                //取得方法界别的RequestMapping  
                RequestMapping mapping = AnnotationUtils.findAnnotation(method, RequestMapping.class);  
                if (mapping != null) {  
                    //获取可以处理的URL  
                    String[] mappedPaths = mapping.value();  
                    //将这些URL放入到可处理的URL集合中  
                    for (String mappedPath : mappedPaths) {  
                        addUrlsForPath(urls, mappedPath);  
                    }  
                }  
            }  
        });  
    }  
    return StringUtils.toStringArray(urls);  
}  
```

分别获取了类和方法级别的RequestMapping中定义的URL后，基本上完成了URL的提取工作，但是有一种情况需要处理：类和方法中同时定义了URL，这两个URL是如何合并的呢？规则又是怎样的呢？看一下URL合并代码,
AntPathMatcher#combine()源码如下，

```java
public String combine(String pattern1, String pattern2) {  
    if (!StringUtils.hasText(pattern1) && !StringUtils.hasText(pattern2)) {  
                    //如果两个URL都为空，那么返回空  
        return "";  
    }  
    else if (!StringUtils.hasText(pattern1)) {  
                    //如果第一个为空，返回第二个  
        return pattern2;  
    }  
    else if (!StringUtils.hasText(pattern2)) {  
                    //如果第二个为空，则返回第一个  
        return pattern1;  
    }  
    else if (match(pattern1, pattern2)) {  
                    //如果两个URL匹配，则返回第二个  
        return pattern2;  
    }  
    else if (pattern1.endsWith("/*")) {  
        if (pattern2.startsWith("/")) {  
            // /hotels/* + /booking -> /hotels/booking  
            return pattern1.substring(0, pattern1.length() - 1) + pattern2.substring(1);  
        }  
        else {  
            // /hotels/* + booking -> /hotels/booking  
            return pattern1.substring(0, pattern1.length() - 1) + pattern2;  
        }  
    }  
    else if (pattern1.endsWith("/**")) {  
        if (pattern2.startsWith("/")) {  
            // /hotels/** + /booking -> /hotels/**/booking  
            return pattern1 + pattern2;  
        }  
        else {  
            // /hotels/** + booking -> /hotels/**/booking  
            return pattern1 + "/" + pattern2;  
        }  
    }  
    else {  
        int dotPos1 = pattern1.indexOf('.');  
        if (dotPos1 == -1) {  
            // simply concatenate the two patterns  
            if (pattern1.endsWith("/") || pattern2.startsWith("/")) {  
                return pattern1 + pattern2;  
            }  
            else {  
                return pattern1 + "/" + pattern2;  
            }  
        }  
        String fileName1 = pattern1.substring(0, dotPos1);  
        String extension1 = pattern1.substring(dotPos1);  
        String fileName2;  
        String extension2;  
        int dotPos2 = pattern2.indexOf('.');  
        if (dotPos2 != -1) {  
            fileName2 = pattern2.substring(0, dotPos2);  
            extension2 = pattern2.substring(dotPos2);  
        }  
        else {  
            fileName2 = pattern2;  
            extension2 = "";  
        }  
        String fileName = fileName1.endsWith("*") ? fileName2 : fileName1;  
        String extension = extension1.startsWith("*") ? extension2 : extension1;  

        return fileName + extension;  
    }  
}  
```

通过以上的处理，基本上完成了bean可以处理的URL信息的提取，在代码中有个方法经常出现：addUrlsForPath()，该方法的目的是将 RequestMapping中定义的path添加的URL集合中，如果指定PATH不是以默认的方式结尾，那么Spring将默认的结尾添加到该 path上，并将处理结果添加到url集合中。
DefaultAnnotationHandlerMapping#addUrlsForPath()源码如下，

```java
protected void addUrlsForPath(Set<String> urls, String path) {  
    urls.add(path);  
    if (this.useDefaultSuffixPattern && path.indexOf('.') == -1 && !path.endsWith("/")) {  
        urls.add(path + ".*");  
        urls.add(path + "/");  
    }  
}  
```

#### 扫描到url和controller及方法映射关系，如何保存？

整合后的url和controller bean的管理会保存在一个map中

```java
private final Map<String, Object> handlerMap = new LinkedHashMap<String, Object>();
```

AbstractUrlHandlerMapping#registerHandler()源码如下，

```java
protected void registerHandler(String[] urlPaths, String beanName) throws BeansException, IllegalStateException {
    Assert.notNull(urlPaths, "URL path array must not be null");
    for (String urlPath : urlPaths) {
        registerHandler(urlPath, beanName);
    }
}
...
/**
 * Register the specified handler for the given URL path.
 * @param urlPath the URL the bean should be mapped to
 * @param handler the handler instance or handler bean name String
 * (a bean name will automatically be resolved into the corresponding handler bean)
 * @throws BeansException if the handler couldn't be registered
 * @throws IllegalStateException if there is a conflicting handler registered
 */
protected void registerHandler(String urlPath, Object handler) throws BeansException, IllegalStateException {
    Assert.notNull(urlPath, "URL path must not be null");
    Assert.notNull(handler, "Handler object must not be null");
    Object resolvedHandler = handler;

    // Eagerly resolve handler if referencing singleton via name.
    if (!this.lazyInitHandlers && handler instanceof String) {
        String handlerName = (String) handler;
        if (getApplicationContext().isSingleton(handlerName)) {
            resolvedHandler = getApplicationContext().getBean(handlerName);
        }
    }

    Object mappedHandler = this.handlerMap.get(urlPath);
    if (mappedHandler != null) {
        if (mappedHandler != resolvedHandler) {
            throw new IllegalStateException(
                    "Cannot map " + getHandlerDescription(handler) + " to URL path [" + urlPath +
                    "]: There is already " + getHandlerDescription(mappedHandler) + " mapped.");
        }
    }
    else {
        if (urlPath.equals("/")) {
            if (logger.isInfoEnabled()) {
                logger.info("Root mapping to " + getHandlerDescription(handler));
            }
            setRootHandler(resolvedHandler);
        }
        else if (urlPath.equals("/*")) {
            if (logger.isInfoEnabled()) {
                logger.info("Default mapping to " + getHandlerDescription(handler));
            }
            setDefaultHandler(resolvedHandler);
        }
        else {
            this.handlerMap.put(urlPath, resolvedHandler);
            if (logger.isInfoEnabled()) {
                logger.info("Mapped URL path [" + urlPath + "] onto " + getHandlerDescription(handler));
            }
        }
    }
}
```

### 根据request查询相应处理controller
从前端控制（DispatcherServlet）开始往后分析，web.xml配置的符合的url都会由DispatcherServlet的doService()处理，
DispatcherServlet#doService()源码如下，

```java
/**
 * Exposes the DispatcherServlet-specific request attributes and delegates to {@link #doDispatch}
 * for the actual dispatching.
 */
@Override
protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
    if (logger.isDebugEnabled()) {
        String resumed = WebAsyncUtils.getAsyncManager(request).hasConcurrentResult() ? " resumed" : "";
        logger.debug("DispatcherServlet with name '" + getServletName() + "'" + resumed +
                " processing " + request.getMethod() + " request for [" + getRequestUri(request) + "]");
    }

    // Keep a snapshot of the request attributes in case of an include,
    // to be able to restore the original attributes after the include.
    Map<String, Object> attributesSnapshot = null;
    if (WebUtils.isIncludeRequest(request)) {
        attributesSnapshot = new HashMap<String, Object>();
        Enumeration<?> attrNames = request.getAttributeNames();
        while (attrNames.hasMoreElements()) {
            String attrName = (String) attrNames.nextElement();
            if (this.cleanupAfterInclude || attrName.startsWith("org.springframework.web.servlet")) {
                attributesSnapshot.put(attrName, request.getAttribute(attrName));
            }
        }
    }

    // Make framework objects available to handlers and view objects.
    request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, getWebApplicationContext());
    request.setAttribute(LOCALE_RESOLVER_ATTRIBUTE, this.localeResolver);
    request.setAttribute(THEME_RESOLVER_ATTRIBUTE, this.themeResolver);
    request.setAttribute(THEME_SOURCE_ATTRIBUTE, getThemeSource());

    FlashMap inputFlashMap = this.flashMapManager.retrieveAndUpdate(request, response);
    if (inputFlashMap != null) {
        request.setAttribute(INPUT_FLASH_MAP_ATTRIBUTE, Collections.unmodifiableMap(inputFlashMap));
    }
    request.setAttribute(OUTPUT_FLASH_MAP_ATTRIBUTE, new FlashMap());
    request.setAttribute(FLASH_MAP_MANAGER_ATTRIBUTE, this.flashMapManager);

    try {
        doDispatch(request, response);
    }
    finally {
        if (WebAsyncUtils.getAsyncManager(request).isConcurrentHandlingStarted()) {
            return;
        }
        // Restore the original attribute snapshot, in case of an include.
        if (attributesSnapshot != null) {
            restoreAttributesAfterInclude(request, attributesSnapshot);
        }
    }
}

/**
 * Process the actual dispatching to the handler.
 * <p>The handler will be obtained by applying the servlet's HandlerMappings in order.
 * The HandlerAdapter will be obtained by querying the servlet's installed HandlerAdapters
 * to find the first that supports the handler class.
 * <p>All HTTP methods are handled by this method. It's up to HandlerAdapters or handlers
 * themselves to decide which methods are acceptable.
 * @param request current HTTP request
 * @param response current HTTP response
 * @throws Exception in case of any kind of processing failure
 */
protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
    HttpServletRequest processedRequest = request;
    HandlerExecutionChain mappedHandler = null;
    boolean multipartRequestParsed = false;

    WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);

    try {
        ModelAndView mv = null;
        Exception dispatchException = null;

        try {
            processedRequest = checkMultipart(request);
            multipartRequestParsed = (processedRequest != request);

            // Determine handler for the current request.
            mappedHandler = getHandler(processedRequest);
            if (mappedHandler == null || mappedHandler.getHandler() == null) {
                noHandlerFound(processedRequest, response);
                return;
            }

            // Determine handler adapter for the current request.
            HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());

            // Process last-modified header, if supported by the handler.
            String method = request.getMethod();
            boolean isGet = "GET".equals(method);
            if (isGet || "HEAD".equals(method)) {
                long lastModified = ha.getLastModified(request, mappedHandler.getHandler());
                if (logger.isDebugEnabled()) {
                    logger.debug("Last-Modified value for [" + getRequestUri(request) + "] is: " + lastModified);
                }
                if (new ServletWebRequest(request, response).checkNotModified(lastModified) && isGet) {
                    return;
                }
            }

            if (!mappedHandler.applyPreHandle(processedRequest, response)) {
                return;
            }

            try {
                // Actually invoke the handler.
                mv = ha.handle(processedRequest, response, mappedHandler.getHandler());
            }
            finally {
                if (asyncManager.isConcurrentHandlingStarted()) {
                    return;
                }
            }

            applyDefaultViewName(request, mv);
            mappedHandler.applyPostHandle(processedRequest, response, mv);
        }
        catch (Exception ex) {
            dispatchException = ex;
        }
        processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException);
    }
    catch (Exception ex) {
        triggerAfterCompletion(processedRequest, response, mappedHandler, ex);
    }
    catch (Error err) {
        triggerAfterCompletionWithError(processedRequest, response, mappedHandler, err);
    }
    finally {
        if (asyncManager.isConcurrentHandlingStarted()) {
            // Instead of postHandle and afterCompletion
            mappedHandler.applyAfterConcurrentHandlingStarted(processedRequest, response);
            return;
        }
        // Clean up any resources used by a multipart request.
        if (multipartRequestParsed) {
            cleanupMultipart(processedRequest);
        }
    }
}
```

doDispatch方法中的mappedHandler = getHandler(processedRequest);就是根据request中的请求url获取相应的controller实例。
DispatcherServlet#getHandler()源码如下，

```java
/** List of HandlerMappings used by this servlet */
private List<HandlerMapping> handlerMappings;

...
	
/**
 * Return the HandlerExecutionChain for this request.
 * <p>Tries all handler mappings in order.
 * @param request current HTTP request
 * @return the HandlerExecutionChain, or {@code null} if no handler could be found
 */
protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
    for (HandlerMapping hm : this.handlerMappings) {
        if (logger.isTraceEnabled()) {
            logger.trace(
                    "Testing handler map [" + hm + "] in DispatcherServlet with name '" + getServletName() + "'");
        }
        HandlerExecutionChain handler = hm.getHandler(request);
        if (handler != null) {
            return handler;
        }
    }
    return null;
}
```

此处的handlerMappings保存的就是所有controller实例.

AbstractHandlerMapping#getHandler()源码如下，

```java
/**
 * Look up a handler for the given request, falling back to the default
 * handler if no specific one is found.
 * @param request current HTTP request
 * @return the corresponding handler instance, or the default handler
 * @see #getHandlerInternal
 */
@Override
public final HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
    Object handler = getHandlerInternal(request);
    if (handler == null) {
        handler = getDefaultHandler();
    }
    if (handler == null) {
        return null;
    }
    // Bean name or resolved handler?
    if (handler instanceof String) {
        String handlerName = (String) handler;
        handler = getApplicationContext().getBean(handlerName);
    }
    return getHandlerExecutionChain(handler, request);
}
```

getHandlerInternal()就是从上面加载保存url和controller对应关系的的handlerMap中取controller实例。
AbstractUrlHandlerMapping#getHandlerInternal()源码如下，

```java
/**
 * Look up a handler for the URL path of the given request.
 * @param request current HTTP request
 * @return the handler instance, or {@code null} if none found
 */
@Override
protected Object getHandlerInternal(HttpServletRequest request) throws Exception {
    String lookupPath = getUrlPathHelper().getLookupPathForRequest(request);
    Object handler = lookupHandler(lookupPath, request);
    if (handler == null) {
        // We need to care for the default handler directly, since we need to
        // expose the PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE for it as well.
        Object rawHandler = null;
        if ("/".equals(lookupPath)) {
            rawHandler = getRootHandler();
        }
        if (rawHandler == null) {
            rawHandler = getDefaultHandler();
        }
        if (rawHandler != null) {
            // Bean name or resolved handler?
            if (rawHandler instanceof String) {
                String handlerName = (String) rawHandler;
                rawHandler = getApplicationContext().getBean(handlerName);
            }
            validateHandler(rawHandler, request);
            handler = buildPathExposingHandler(rawHandler, lookupPath, lookupPath, null);
        }
    }
    if (handler != null && logger.isDebugEnabled()) {
        logger.debug("Mapping [" + lookupPath + "] to " + handler);
    }
    else if (handler == null && logger.isTraceEnabled()) {
        logger.trace("No handler mapping found for [" + lookupPath + "]");
    }
    return handler;
}
/**
 * Look up a handler instance for the given URL path.
 * <p>Supports direct matches, e.g. a registered "/test" matches "/test",
 * and various Ant-style pattern matches, e.g. a registered "/t*" matches
 * both "/test" and "/team". For details, see the AntPathMatcher class.
 * <p>Looks for the most exact pattern, where most exact is defined as
 * the longest path pattern.
 * @param urlPath URL the bean is mapped to
 * @param request current HTTP request (to expose the path within the mapping to)
 * @return the associated handler instance, or {@code null} if not found
 * @see #exposePathWithinMapping
 * @see org.springframework.util.AntPathMatcher
 */
protected Object lookupHandler(String urlPath, HttpServletRequest request) throws Exception {
    // Direct match?
    Object handler = this.handlerMap.get(urlPath);
    if (handler != null) {
        // Bean name or resolved handler?
        if (handler instanceof String) {
            String handlerName = (String) handler;
            handler = getApplicationContext().getBean(handlerName);
        }
        validateHandler(handler, request);
        return buildPathExposingHandler(handler, urlPath, urlPath, null);
    }
    // Pattern match?
    List<String> matchingPatterns = new ArrayList<String>();
    for (String registeredPattern : this.handlerMap.keySet()) {
        if (getPathMatcher().match(registeredPattern, urlPath)) {
            matchingPatterns.add(registeredPattern);
        }
    }
    String bestPatternMatch = null;
    Comparator<String> patternComparator = getPathMatcher().getPatternComparator(urlPath);
    if (!matchingPatterns.isEmpty()) {
        Collections.sort(matchingPatterns, patternComparator);
        if (logger.isDebugEnabled()) {
            logger.debug("Matching patterns for request [" + urlPath + "] are " + matchingPatterns);
        }
        bestPatternMatch = matchingPatterns.get(0);
    }
    if (bestPatternMatch != null) {
        handler = this.handlerMap.get(bestPatternMatch);
        // Bean name or resolved handler?
        if (handler instanceof String) {
            String handlerName = (String) handler;
            handler = getApplicationContext().getBean(handlerName);
        }
        validateHandler(handler, request);
        String pathWithinMapping = getPathMatcher().extractPathWithinPattern(bestPatternMatch, urlPath);

        // There might be multiple 'best patterns', let's make sure we have the correct URI template variables
        // for all of them
        Map<String, String> uriTemplateVariables = new LinkedHashMap<String, String>();
        for (String matchingPattern : matchingPatterns) {
            if (patternComparator.compare(bestPatternMatch, matchingPattern) == 0) {
                Map<String, String> vars = getPathMatcher().extractUriTemplateVariables(matchingPattern, urlPath);
                Map<String, String> decodedVars = getUrlPathHelper().decodePathVariables(request, vars);
                uriTemplateVariables.putAll(decodedVars);
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("URI Template variables for request [" + urlPath + "] are " + uriTemplateVariables);
        }
        return buildPathExposingHandler(handler, bestPatternMatch, pathWithinMapping, uriTemplateVariables);
    }
    // No handler found...
    return null;
}
```

### spring mvc 获取所有的controller和url映射关系
有时候需要根据url反查controller，如果能获取所有的url，则不用跟据url去代码里搜了，方便开发人员、调试人员或交接人。
关键对象：RequestMappingHandlerMapping 

```java
import java.util.ArrayList;  
import java.util.HashMap;  
import java.util.List;  
import java.util.Map;  
  
import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.stereotype.Controller;  
import org.springframework.ui.Model;  
import org.springframework.web.bind.annotation.RequestMapping;  
import org.springframework.web.method.HandlerMethod;  
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;  
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;  
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;  
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;  
  
@Controller  
public class MappingController {  
  
    @Autowired  
    private RequestMappingHandlerMapping requestMappingHandlerMapping;  
  
    @RequestMapping(value = "/mappings")  
    public String list(Model model) {  
        List<HashMap<String, String>> urlList = new ArrayList<HashMap<String, String>>();  
  
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();  
        for (Map.Entry<RequestMappingInfo, HandlerMethod> m : map.entrySet()) {  
            HashMap<String, String> hashMap = new HashMap<String, String>();  
            RequestMappingInfo info = m.getKey();  
            HandlerMethod method = m.getValue();  
            PatternsRequestCondition p = info.getPatternsCondition();  
            for (String url : p.getPatterns()) {  
                hashMap.put("url", url);  
            }  
            hashMap.put("className", method.getMethod().getDeclaringClass().getName()); // 类名  
            hashMap.put("method", method.getMethod().getName()); // 方法名  
            RequestMethodsRequestCondition methodsCondition = info.getMethodsCondition();  
            String type = methodsCondition.toString();  
            if (type != null && type.startsWith("[") && type.endsWith("]")) {  
                type = type.substring(1, type.length() - 1);  
                hashMap.put("type", type); // 方法名  
            }  
            urlList.add(hashMap);  
        }  
        model.addAttribute("list", urlList);  
        return "/console/system/mappingList";  
    }  
  
}  
```