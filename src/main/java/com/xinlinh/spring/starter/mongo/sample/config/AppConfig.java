package com.xinlinh.spring.starter.mongo.sample.config;

import com.xinlinh.spring.starter.mongo.annotation.EnableMongoClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @ClassName: AppConfig
 * @Description: Spring配置入口
 * @Author:xinlinh
 * @Date: 2021/1/11 17:31
 * @Version: 1.0
 **/
@Configuration
@PropertySource("classpath:mongo-client.properties")
@EnableMongoClient(namespace = "demo")
public class AppConfig {
}
