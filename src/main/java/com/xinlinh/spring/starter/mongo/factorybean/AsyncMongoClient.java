package com.xinlinh.spring.starter.mongo.factorybean;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.FactoryBean;

/**
 * @ClassName: AsyncMongoClient
 * @Description: TODO
 * @Author:xinlinh
 * @Date: 2021/1/11 17:12
 * @Version: 1.0
 **/
public class AsyncMongoClient implements FactoryBean<MongoClient> {

    private String connectionString;
    private boolean enablePojoCodecRegistry;

    public AsyncMongoClient(String connectionString,boolean enablePojoCodecRegistry) {
        this.connectionString = connectionString;
        this.enablePojoCodecRegistry = enablePojoCodecRegistry;
    }

    @Override
    public MongoClient getObject() throws Exception {
        if(enablePojoCodecRegistry) {
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                    CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        return MongoClients.create(MongoClientSettings.builder().codecRegistry(pojoCodecRegistry).applyConnectionString(new ConnectionString(connectionString)).build());
        }
        return MongoClients.create(connectionString);
    }

    @Override
    public Class<?> getObjectType() {
        return MongoClient.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
