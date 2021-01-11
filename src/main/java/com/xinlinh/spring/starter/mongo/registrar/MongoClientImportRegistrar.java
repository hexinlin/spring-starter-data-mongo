package com.xinlinh.spring.starter.mongo.registrar;

import com.mongodb.*;
import com.xinlinh.spring.starter.mongo.annotation.EnableMongoClient;
import com.xinlinh.spring.starter.mongo.factorybean.AsyncMongoClient;
import com.xinlinh.spring.starter.mongo.factorybean.SyncMongoClient;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

/**
 * @Author xinlinh
 * @Description 通过Import向Spring容器中注入MongoClient
 * @Date 2021/1/11 17:30
 * @Param
 * @return
 **/
public class MongoClientImportRegistrar implements ImportBeanDefinitionRegistrar,EnvironmentAware {

    private Environment environment;
    private String namespace;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Map<String, Object> annotationAttributes =  importingClassMetadata.getAnnotationAttributes(EnableMongoClient.class.getName());
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(annotationAttributes);
        namespace = attributes.getString("namespace");
        boolean async = attributes.getBoolean("async");
        boolean enablePojoCodecRegistry = attributes.getBoolean("enablePojoCodecRegistry");


        String connectionString = getMongoClientConnetionString();
        BeanDefinitionBuilder beanDefinitionBuilder = null;
        if(async) {
            beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(AsyncMongoClient.class);
        }else {
            beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(SyncMongoClient.class);
        }
        beanDefinitionBuilder.addConstructorArgValue(connectionString);
        beanDefinitionBuilder.addConstructorArgValue(enablePojoCodecRegistry);

        BeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();

        if(async) {
            registry.registerBeanDefinition(String.format("%s%s",namespace,AsyncMongoClient.class.getSimpleName()),beanDefinition);
        }else {
            registry.registerBeanDefinition(String.format("%s%s",namespace,SyncMongoClient.class.getSimpleName()),beanDefinition);
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }



    /**
     * @Author xinlinh
     * @Description 从环境变量中获取mongoClient的连接配置信息
     * @Date 2021/1/11 16:59
     * @Param []
     * @return java.lang.String
     **/
    private String getMongoClientConnetionString() {
       return environment.getProperty(getFullName("connectionString"),"mongodb://localhost");
    }

    private String getFullName (String name) {
        return String.format("%s.mongo.%s",namespace,name);
    }
}
