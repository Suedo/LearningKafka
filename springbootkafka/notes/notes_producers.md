### Producers

`@Autowire` in a `KafkaTemplate` and use one of it's many overloaded `send()` methods. 
The `send()` methods all return a `ListenableFuture<SendResult<K,V>>` , where K and V are the types of the key and value being used.  
Then use the `ListenableFuture` instance's `addCallback` method to add a callback function for Success and Failure scenarios

From the `ListenableFuture` type, we understand that the above flow was `async` . If you want a synchronous flow, then call the `KafkaTemplate.sendDefault().get()` method. This one returns a `SendResult<K,V>` instead.

Note: 

- `sendDefault()` will send the message to the default configured topic.
- `send()` takes a topic as an argument, and thus can send the message to any topic you choose

Here's an idiom that I like, for sending messages async-ly, to a supplied topic, with headers:

```java
// LibraryEvent is some business model object
public void sendAsync(LibraryEvent libraryEvent, String topic) throws JsonProcessingException {
    Integer key = libraryEvent.getLibraryEventId();
    String value = objectMapper.writeValueAsString(libraryEvent);

    ProducerRecord<Integer, String> pr = buildProducerRecord(key, value, topic);
    ListenableFuture<SendResult<Integer, String>> sr = kafkaTemplate.send(pr);
    sr.addCallback(new LibraryEventListenableFutureCallback(key, value));
}

private ProducerRecord<Integer, String> buildProducerRecord(Integer key, String value, String topic) {
    // we can also add some headers to the message
    List<Header> headerList = List.of(new RecordHeader("event-source", "scanner".getBytes()));
    return new ProducerRecord<>(topic, null, key, value, headerList);
}
```