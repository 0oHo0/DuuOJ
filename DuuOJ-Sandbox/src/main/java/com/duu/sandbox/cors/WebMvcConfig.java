package com.duu.sandbox.cors;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    /*
        静态资源映射
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/image/**").addResourceLocations("classpath:/image/");

    }
//
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //设置允许跨域的路径
        registry.addMapping("/**")
                //设置允许跨域请求的域名
                //当**Credentials为true时，**Origin不能为星号，需为具体的ip地址【如果接口不带cookie,ip无需设成具体ip】
                .allowedOrigins("*")
                //是否允许证书 不再默认开启
                .allowCredentials(false)
                //设置允许的方法
                .allowedMethods("*");
                //跨域允许时间
                //.maxAge(3600);
    }
    /**
     * 扩展mvc框架的消息转换器
     * @param converters
     */
//    @Override
//    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
//        log.info("扩展消息转换器...");
//        //创建消息转换器对象
//        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
//        //设置对象转换器，底层使用Jackson将Java对象转为json
//        messageConverter.setObjectMapper(new JacksonObjectMapper());
//        //将上面的消息转换器对象追加到mvc框架的转换器集合中
//        converters.add(0,messageConverter);
//    }
}

