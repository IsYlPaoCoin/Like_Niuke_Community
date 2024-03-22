package com.nowcoder.community.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

/**
 * ClassName: AlphaConfig
 * Package: com.nowcoder.community.community.config
 * Description:
 *
 * @Author 杨理
 * @Create 2024/3/20 19:40
 * @Version 1.0
 */

//表示这个类是 一个配置类
@Configuration
public class AlphaConfig {

    //这段方法   返回的对象  将会被装配到容器里
    @Bean
    public SimpleDateFormat simpleDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

}
