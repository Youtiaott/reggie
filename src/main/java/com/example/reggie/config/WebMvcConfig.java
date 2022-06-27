package com.example.reggie.config;

import com.example.reggie.interceptor.LoginInterceptor;
import com.example.reggie.common.JacksonObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mobile.device.DeviceHandlerMethodArgumentResolver;
import org.springframework.mobile.device.DeviceResolverHandlerInterceptor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * description: mcv配置类
 * date: 2022/6/10 10:39
 * version: 1.0
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    LoginInterceptor loginInterceptor;

    //用来指定静态资源不被拦截
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");

        WebMvcConfigurer.super.addResourceHandlers(registry);
    }

    //添加拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        //需要放行的资源
        List<String> list = new ArrayList<>();
        list.add("/backend/**");
        list.add("/employee/login");
        list.add("/front/**");
        list.add("/user/login");//移动端登陆
        list.add("/user/sendMsg");//移动端发短信
        //list.add("/index");

        //判断用户设备的拦截器
        registry.addInterceptor(new DeviceResolverHandlerInterceptor());

        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")//拦截
                .excludePathPatterns(list);//放行


    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new DeviceHandlerMethodArgumentResolver());
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("forward:/backend/page/login.html");
        WebMvcConfigurer.super.addViewControllers(registry);
    }

    /***
     * @Description //TODO 扩展mvc消息转换器
     * @param converters
     * @return: void
     **/
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用jackson将java对象转为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将上面的消息转换器追加到mvc的转换器集合中；index：0  将自己设置的转换器放在最前面
        converters.add(0,messageConverter);
    }

}
