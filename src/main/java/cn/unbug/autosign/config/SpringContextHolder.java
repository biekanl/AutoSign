package cn.unbug.autosign.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.util.Locale;

/**
 * bean 工具类
 *
 * @author zhangtao
 */

@Slf4j
@Component
public final class SpringContextHolder implements BeanFactoryPostProcessor, ApplicationContextAware {

    /**
     * ApplicationContext
     */
    private static ApplicationContext context;

    /**
     * ServletContext
     */
    private static ServletContext servletContext;

    /**
     * Environment
     */
    private static Environment environment;

    /**
     * MessageSourceAccessor
     */
    private static MessageSourceAccessor messages;

    /**
     * Constructor<br>
     * 私有化工具类的构造函数
     */
    private SpringContextHolder() {
    }

    /**
     * get ApplicationContext<br>
     *
     * @return ApplicationContext
     */
    public static ApplicationContext getSpringContext() {

        return context;
    }

    /**
     * get {@link ServletContext}<br>
     *
     * @return {@link ServletContext}
     */
    public static ServletContext getServletContext() {

        return servletContext;
    }

    /**
     * set Servlet Context<br>
     *
     * @param sc ServletContext
     */
    public static void setServletContext(ServletContext sc) {

        servletContext = sc;
    }

    /**
     * get Environment<br/>
     *
     * @return
     */
    public static Environment getEnvironment() {

        return environment;
    }

    /**
     * 根据名字获得spring context中的bean<br>
     *
     * @param name bean的名称
     * @return bean
     */
    public static Object getBean(String name) {

        return context.getBean(name);
    }

    /**
     * 根据类型获得spring context中的bean<br>
     *
     * @param requiredType bean的类型
     * @return bean
     */
    public static <T> T getBean(Class<T> requiredType) {

        return context.getBean(requiredType);
    }

    /**
     * 根据名称和类型获得spring context中的bean<br>
     *
     * @param name         bean 的名称
     * @param requiredType bean的类型
     * @return bean
     */
    public static <T> T getBean(String name, Class<T> requiredType) {

        return context.getBean(name, requiredType);
    }

    /**
     * 获取properties的值，没有获取到返回null<br>
     *
     * @return 该key对应的value值
     */
    public static String getProperty(String key) {

        return environment.getProperty(key);
    }

    /**
     * 获取properties的值，没有获取到抛出异常<br>
     *
     * @return 该key对应的value值
     * @throws IllegalStateException if the key cannot be resolved
     */
    public static String getRequiredProperty(String key) {

        return environment.getRequiredProperty(key);
    }

    /**
     * 获取国际化访问工具<br>
     *
     * @return 国际化访问工具
     */
    public static MessageSourceAccessor getMessageSourceAccessor() {

        return messages;
    }

    /**
     * 对相关的属性进行赋值<br/>
     *
     * @param applicationContext ApplicationContext
     */
    private static void init(ApplicationContext applicationContext) {

        context = applicationContext;
        environment = context.getEnvironment();

        if (context instanceof WebApplicationContext) {

            servletContext = ((WebApplicationContext) context).getServletContext();
        }

        messages = new MessageSourceAccessor(context, Locale.SIMPLIFIED_CHINESE);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {

        init(applicationContext);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {

        log.info("Spring context holder initialized successful");
    }
}
