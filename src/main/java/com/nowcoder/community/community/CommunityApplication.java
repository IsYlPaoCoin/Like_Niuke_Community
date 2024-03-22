package com.nowcoder.community.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CommunityApplication {

    public static void main(String[] args) {
        //做的事情
        //1.启动tomcat
        //2.自动创建了Spring容器   == 》     创建以后会自动的扫描   某些包(配置类所在包 以及其子包)下的Bean  装配到容器中
        SpringApplication.run(CommunityApplication.class, args);

    }

}
