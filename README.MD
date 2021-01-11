# spring-stater-data-mongo
> 基于mongo java driver开发的一个mongoClient插件。使用@EnableMongoClient注解即可快速生成存在于spring容器中的单例MongoClient bean。
## 依赖包
> 所有包的scope都为provided，当用于项目中时，需要自行在工程中指定相应版本的jar包。
```xml
<dependencies>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>5.3.2</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.mongodb</groupId>
            <artifactId>mongodb-driver-sync</artifactId>
            <version>4.1.1</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.mongodb</groupId>
            <artifactId>mongodb-driver-reactivestreams</artifactId>
            <version>4.1.1</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
```
## @EnableMongoClient注解
```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({MongoClientImportRegistrar.class})
public @interface EnableMongoClient {

    String namespace() default "default";

    boolean async() default false;

    boolean enablePojoCodecRegistry() default true;
}

```
此注解包含三个属性：namespace、async、enablePojoCodecRegistry：
* namespace：命名空间，用于区分一个项目中有多个mongo client的情况。
* async：是否使用异步的反应式编程模式下的mongo client，默认为false，代表使用同步模式的mongo client。
* enablePojoCodecRegistry： 是否开启POJO的编解码器，默认为true，代表开启，可指定将mongo的document映射为java的POJO。

## MongoClientImportRegistrar
> 利用@Import注解的特性，使用实现了ImportBeanDefinitionRegistrar接口的MongoClientImportRegistrar类，向Spring容器中注入MongoClient。
* 通过@EnableMongoClient注解的属性信息，向Spring中加入不同的beanDefinition。
* 同步的MongoClient通过SyncMongoClient工厂类实现。
```java
 @Override
    public MongoClient getObject() throws Exception {
        if(enablePojoCodecRegistry) {
            CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                    CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
            return MongoClients.create(MongoClientSettings.builder().codecRegistry(pojoCodecRegistry).applyConnectionString(new ConnectionString(connectionString)).build());
        }
        return MongoClients.create(connectionString);
    }
```
* 异步的MongoClient通过ASyncMongoClient工厂类实现。
```java
@Override
    public MongoClient getObject() throws Exception {
        if(enablePojoCodecRegistry) {
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                    CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        return MongoClients.create(MongoClientSettings.builder().codecRegistry(pojoCodecRegistry).applyConnectionString(new ConnectionString(connectionString)).build());
        }
        return MongoClients.create(connectionString);
    }
```
## 示例
> sample包下提供了一个使用示例。
* Spring入口配置文件AppConfig:
```java
@Configuration
@PropertySource("classpath:mongo-client.properties")
@EnableMongoClient(namespace = "demo")
public class AppConfig {
}
```
* 测试同步查询collection：
```java
//@EnableMongoClient(namespace = "demo",async = false)
    private void testSyncMongoClient() {
        MongoClient mongoClient = context.getBean(MongoClient.class);
        MongoDatabase database =  mongoClient.getDatabase("mpp");
        MongoCollection collection =  database.getCollection("people");
        collection.find().forEach(System.out::println);
    }
```
* 测试同步查询POJO型的collection：
```java
 //@EnableMongoClient(namespace = "demo",async = false ,enablePojoCodecRegistry = true)
    private void testSyncMongoClientPojo() {
        MongoClient mongoClient = context.getBean(MongoClient.class);
        MongoDatabase database =  mongoClient.getDatabase("mpp");
        MongoCollection<Person> collection =  database.getCollection("people",Person.class);
        collection.find().forEach(System.out::println);
    }
```
* 测试异步查询collection:
```java
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
```
* 测试异步查询POJO型的collection：
```
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
```
## 直接可使用的jar包
> resources目录下的spring-starter-data-mongo-1.0.0.jar。
* maven配置：
```xml
<dependency>
    <groupId>com.xinlinh</groupId>
    <artifactId>spring-starter-data-mongo</artifactId>
    <version>1.0.0</version>
</dependency>
```
## github源码地址：
> https://github.com/hexinlin/spring-starter-data-mongo.git