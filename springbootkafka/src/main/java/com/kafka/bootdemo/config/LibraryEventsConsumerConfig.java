package com.kafka.bootdemo.config;

import com.kafka.bootdemo.service.LibraryEventService;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

import static com.kafka.bootdemo.config.ConfigHelper.retryTemplate;

@Configuration
@EnableKafka
@Log4j2
public class LibraryEventsConsumerConfig {

    private final KafkaProperties properties = new KafkaProperties(); // contains application.yml configs
    private final int numOfPartions = 3;
    @Autowired
    LibraryEventService libraryEventService;

    // we want ack mode to be manual, so we need to override kafkaListenerContainerFactory from KafkaAnnotationDrivenConfiguration class
    @Bean
    @ConditionalOnMissingBean(name = {"kafkaListenerContainerFactory"})
    ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(ConcurrentKafkaListenerContainerFactoryConfigurer configurer, ObjectProvider<ConsumerFactory<Object, Object>> kafkaConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory();
        configurer.configure(factory, kafkaConsumerFactory.getIfAvailable(() -> {
            return new DefaultKafkaConsumerFactory(this.properties.buildConsumerProperties());
        }));
        // factory.setConcurrency(numOfPartions); // concurrent mode, not useful in cloud based kafka running in kubernetes etc
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        factory.setRetryTemplate(retryTemplate());
        factory.setRecoveryCallback((context -> {
            if (context.getLastThrowable().getCause() instanceof RecoverableDataAccessException) {
                // recovery logic
                log.info("Inside recoverable logic");
                // Arrays.asList(context.attributeNames()).forEach(name -> {
                //    log.info("Attribute name: {}", name); // record, consumer, context.exhausted
                //    log.info("Attribute Value: {}", context.getAttribute(name));
                // });
                ConsumerRecord<Integer, String> record = (ConsumerRecord<Integer, String>) context.getAttribute("record");
                libraryEventService.handleRecovery(record);

            } else {
                log.info("Inside non recoverable logic");
                throw new RuntimeException(context.getLastThrowable().getMessage());
            }
            return null;
        }));

        return factory;
    }

}
