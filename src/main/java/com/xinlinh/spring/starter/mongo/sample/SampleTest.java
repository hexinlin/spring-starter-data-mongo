package com.xinlinh.spring.starter.mongo.sample;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.xinlinh.spring.starter.mongo.sample.config.AppConfig;
import com.xinlinh.spring.starter.mongo.sample.domain.Person;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.concurrent.CountDownLatch;

/**
 * @ClassName: SampleTest
 * @Description: 测试类
 * @Author:xinlinh
 * @Date: 2021/1/11 17:34
 * @Version: 1.0
 **/
public class SampleTest implements AutoCloseable{
    private AnnotationConfigApplicationContext context = null;


    public static void main(String[] args) throws Exception{
        try(SampleTest sampleTest = new SampleTest()) {
            sampleTest.testSyncMongoClient();
            //sampleTest.testSyncMongoClientPojo();
            //sampleTest.testAsyncMongoClient();
            //sampleTest.testAsyncMongoClientPojo();
        }
    }

    //@EnableMongoClient(namespace = "demo",async = false)
    private void testSyncMongoClient() {
        MongoClient mongoClient = context.getBean(MongoClient.class);
        MongoDatabase database =  mongoClient.getDatabase("mpp");
        MongoCollection collection =  database.getCollection("people");
        collection.find().forEach(System.out::println);
    }
    //@EnableMongoClient(namespace = "demo",async = false ,enablePojoCodecRegistry = true)
    private void testSyncMongoClientPojo() {
        MongoClient mongoClient = context.getBean(MongoClient.class);
        MongoDatabase database =  mongoClient.getDatabase("mpp");
        MongoCollection<Person> collection =  database.getCollection("people",Person.class);
        collection.find().forEach(System.out::println);
    }
    //@EnableMongoClient(namespace = "demo",async = true)
    private void testAsyncMongoClient() {
        com.mongodb.reactivestreams.client.MongoClient mongoClient = context.getBean(com.mongodb.reactivestreams.client.MongoClient.class);
        com.mongodb.reactivestreams.client.MongoDatabase database = mongoClient.getDatabase("mpp");
        com.mongodb.reactivestreams.client.MongoCollection collection = database.getCollection("people");
        CountDownLatch countDownLatch = new CountDownLatch(1);
        collection.find().subscribe(new Subscriber() {
            @Override
            public void onSubscribe(Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Object o) {
                System.out.println(String.format("receive data: {%s}",o));
            }

            @Override
            public void onError(Throwable throwable) {
                System.err.println(String.format("failed: {%s}",throwable));
                countDownLatch.countDown();
            }

            @Override
            public void onComplete() {
                System.out.println("completed");
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    //@EnableMongoClient(namespace = "demo",async = true, enablePojoCodecRegistry = true)
    private void testAsyncMongoClientPojo() {
        com.mongodb.reactivestreams.client.MongoClient mongoClient = context.getBean(com.mongodb.reactivestreams.client.MongoClient.class);
        com.mongodb.reactivestreams.client.MongoDatabase database = mongoClient.getDatabase("mpp");
        com.mongodb.reactivestreams.client.MongoCollection<Person> collection = database.getCollection("people", Person.class);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        collection.find().subscribe(new Subscriber<Person>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Person person) {
                System.out.println(String.format("receive data: {%s}",person));
            }

            @Override
            public void onError(Throwable throwable) {
                System.err.println(String.format("failed: {%s}",throwable));
                countDownLatch.countDown();
            }

            @Override
            public void onComplete() {
                System.out.println("completed");
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public SampleTest() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
    }

    @Override
    public void close() throws Exception {
        context.registerShutdownHook();
    }
}
