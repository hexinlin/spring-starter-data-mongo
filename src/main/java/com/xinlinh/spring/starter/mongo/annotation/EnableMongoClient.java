package com.xinlinh.spring.starter.mongo.annotation;

import com.xinlinh.spring.starter.mongo.registrar.MongoClientImportRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({MongoClientImportRegistrar.class})
public @interface EnableMongoClient {

    String namespace() default "default";

    boolean async() default false;

    boolean enablePojoCodecRegistry() default true;
}
