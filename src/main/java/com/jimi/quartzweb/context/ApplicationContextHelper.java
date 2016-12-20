package com.jimi.quartzweb.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author jimi
 * @description
 * @date 2016-04-08 11:08.
 */
public class ApplicationContextHelper implements ApplicationContextAware {
    private static ApplicationContext applicationContext = null;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 通过名字获取spring容器中bean实例
     * @param beanName  beanName
     * @return  实例对象
     */
    public static Object getBean(String beanName){
        return applicationContext.getBean(beanName);
    }
}
