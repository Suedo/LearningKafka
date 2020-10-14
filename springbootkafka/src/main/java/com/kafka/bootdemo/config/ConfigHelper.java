package com.kafka.bootdemo.config;

import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;

public class ConfigHelper {

    public static RetryTemplate retryTemplate() {

        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(2000); // 1000 is default

        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(simpleRetryPolicy());
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

        return retryTemplate;
    }

    private static RetryPolicy simpleRetryPolicy() {

        Map<Class<? extends Throwable>, Boolean> exceptionsMap = new HashMap<>();
        exceptionsMap.put(IllegalArgumentException.class, false); // don't retry
        exceptionsMap.put(RecoverableDataAccessException.class, true); // do retry, 3 times, then fail/throw exception

        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(3, exceptionsMap, true);
        return simpleRetryPolicy;
    }
}

/*

Tries to reconnect 3 times when error values provided, in this case we provided a libraryEventId=11, which doesn't exist

2020-10-12 15:13:54.948  INFO 99354 --- [ntainer#0-0-C-1] .k.b.c.LibraryEventsConsumerManualOffset : Consumer Record: ConsumerRecord(topic = library-events, partition = 1, leaderEpoch = 0, offset = 5, CreateTime = 1602495814397, serialized key size = 4, serialized value size = 139, headers = RecordHeaders(headers = [RecordHeader(key = event-source, value = [115, 99, 97, 110, 110, 101, 114])], isReadOnly = false), key = 11, value = {"libraryEventId":11,"type":"UPDATE","book":{"bookId":1,"bookName":"Kafka Using Spring Boot 2","bookAuthor":"Dilip S","libraryEvent":null}})
2020-10-12 15:13:54.949  INFO 99354 --- [ntainer#0-0-C-1] c.k.b.service.LibraryEventService        : LibraryEvent consumed: LibraryEvent(libraryEventId=11, type=UPDATE)
.
.
2020-10-12 15:13:56.952  INFO 99354 --- [ntainer#0-0-C-1] .k.b.c.LibraryEventsConsumerManualOffset : Consumer Record: ConsumerRecord(topic = library-events, partition = 1, leaderEpoch = 0, offset = 5, CreateTime = 1602495814397, serialized key size = 4, serialized value size = 139, headers = RecordHeaders(headers = [RecordHeader(key = event-source, value = [115, 99, 97, 110, 110, 101, 114])], isReadOnly = false), key = 11, value = {"libraryEventId":11,"type":"UPDATE","book":{"bookId":1,"bookName":"Kafka Using Spring Boot 2","bookAuthor":"Dilip S","libraryEvent":null}})
2020-10-12 15:13:56.953  INFO 99354 --- [ntainer#0-0-C-1] c.k.b.service.LibraryEventService        : LibraryEvent consumed: LibraryEvent(libraryEventId=11, type=UPDATE)
.
.
2020-10-12 15:13:58.959  INFO 99354 --- [ntainer#0-0-C-1] .k.b.c.LibraryEventsConsumerManualOffset : Consumer Record: ConsumerRecord(topic = library-events, partition = 1, leaderEpoch = 0, offset = 5, CreateTime = 1602495814397, serialized key size = 4, serialized value size = 139, headers = RecordHeaders(headers = [RecordHeader(key = event-source, value = [115, 99, 97, 110, 110, 101, 114])], isReadOnly = false), key = 11, value = {"libraryEventId":11,"type":"UPDATE","book":{"bookId":1,"bookName":"Kafka Using Spring Boot 2","bookAuthor":"Dilip S","libraryEvent":null}})
2020-10-12 15:13:58.960  INFO 99354 --- [ntainer#0-0-C-1] c.k.b.service.LibraryEventService        : LibraryEvent consumed: LibraryEvent(libraryEventId=11, type=UPDATE)
.
.
2020-10-12 15:13:58.964 ERROR 99354 --- [ntainer#0-0-C-1] essageListenerContainer$ListenerConsumer : Error handler threw an exception

 */