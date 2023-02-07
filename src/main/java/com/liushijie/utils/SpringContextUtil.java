package com.liushijie.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author huki
 * date 2020-06-15
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext context = null;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    /**
     * 获取ApplicationContext容器
     *
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return context;
    }

    /**
     * 获取spring.profiles.active
     *
     * @return spring.profiles.active数组
     */
    public static String[] getActiveProfile() {
        return context.getEnvironment().getActiveProfiles();
    }

    /**
     * 获取配置文件配置项的值
     *
     * @param key 配置项key
     * @return
     */
    public static String getEnvironmentProperty(String key) {
        return getApplicationContext().getEnvironment().getProperty(key);
    }


    /**
     * 根据beanName获取Bean
     *
     * @param name beanName
     * @return
     */
    public static Object getBean(String name) {
        Object o = null;
        try {
            o = getApplicationContext().getBean(name);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
        return o;
    }

    /**
     * 通过class获取bean
     *
     * @param clazz class
     * @param <T>   返回的类型
     * @return
     */
    public static <T> T getBean(Class<T> clazz) {
        try {
            return getApplicationContext().getBean(clazz);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     *
     * @param name  beanName
     * @param clazz class类型
     * @param <T>   返回的类型
     * @return
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        try {
            return getApplicationContext().getBean(name, clazz);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    /**
     * 通过name获取 Bean.
     * 可能返回多个
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        try {
            return getApplicationContext().getBeansOfType(clazz);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }
}

