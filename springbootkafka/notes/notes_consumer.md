MessageListenerContainer is the interface implemented by
    KafkaMessageListenerContainer           // Single threader
    ConcurrentMessageListenerContainer      // behaves as multiple KafkaMessageListenerContainer 

@KafkaListener uses ConcurrentMessageListenerContainer

We setup Kafka Consumers with the @KafkaListener annotation on methods. The class has to be annotated with @Component
Below is a simple Kafka Consumer, with auto acknowledge
```java
@Component 
public class LibraryEventsConsumer {
    @KafkaListener(topics = {"topic-name"})
    public void onMessage(ConsumerRecord<Integer, String> consumerRecord) {
        log.info("Consumer Record: {}", consumerRecord);
    }
}
```
This is a manual acknowledge kafka consumer
```java
@Component 
public class LibraryEventsConsumer implements AcknowledgingMessageListener<K, V> {
    @KafkaListener(topics = {"topic-name"})
    public void onMessage(ConsumerRecord<K, V> consumerRecord, Acknowledgment acknowledgment) {
      ...
      someEventService.processEvent(consumerRecord);
      ...
      acknowledgment.acknowledge(); // manual ack
    } 
}
```

Basic Configuration:

The non Auto Configuration method, where we have a consumerConfigMethod() that manually fetches properties from somewhere, 
and we pass that to the  DefaultKafkaConsumerFactory, thus creating a ConsumerFactory, which gets set to the containerFactory:
 ConcurrentKafkaListenerContainerFactory. Please note, creating a ConsumerFactory will be generally done through a @Bean,
 and that will be setter-injected into the `factory.setConsumerFactory(factoryhere)` call/method. 
```java
@Configuration
@EnableKafka
@Log4j2
public class LibraryEventsConsumerConfig {
    // create a ConcurrentKafkaListenerContainerFactory
    ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory();
    // create a DefaultKafkaConsumerFactory, generally this is a @Bean, but keeping things simple for notes verbosity
    ConsumerFactory<K, V> cf = new DefaultKafkaConsumerFactory(consumerConfigMethod())
    // assign the ConsumerFactory to the ContainerFactory
    factory.setConsumerFactory(cf)
    // assign the retry template and recovery callback
    factory.setRetryTemplate(retryTemplateCreationMethod());
    factory.setRecoveryCallback((context -> { ... })
}
```

If we are using Auto-Configuration, the above logic stays mostly same, the main difference is we obtain configs from properties file 
using the KafkaProperties class, and it's buildConsumerProperties() method

```java
@Configuration
@EnableKafka
@Log4j2
public class LibraryEventsConsumerConfig {
    private final KafkaProperties properties = new KafkaProperties(); // contains application.yml configs
    ...      
    @Bean
    @ConditionalOnMissingBean(name = {"kafkaListenerContainerFactory"})
    ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(ConcurrentKafkaListenerContainerFactoryConfigurer configurer, ObjectProvider<ConsumerFactory<Object, Object>> kafkaConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory();
        configurer.configure(factory, kafkaConsumerFactory.getIfAvailable(() -> {
            return new DefaultKafkaConsumerFactory(this.properties.buildConsumerProperties()); // get application.yml configs
        }));
        // factory.setConcurrency(numOfPartions); // concurrent mode, not useful in cloud based kafka running in kubernetes etc
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        factory.setRetryTemplate(retryTemplate());
        factory.setRecoveryCallback((context -> {...}));
        return factory;
    }

}
```
