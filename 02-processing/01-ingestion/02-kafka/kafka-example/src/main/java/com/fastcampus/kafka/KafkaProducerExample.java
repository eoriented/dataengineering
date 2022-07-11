package com.fastcampus.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class KafkaProducerExample {
  public static void main(String[] args) throws Exception {
    Properties props = new Properties();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    props.put(ProducerConfig.CLIENT_ID_CONFIG, "ProducerExample");
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

    KafkaProducer<String, String> producer = new KafkaProducer<>(props);
    producer.send(new ProducerRecord<>("test-topic", "world"), (recordMetadata, exception) -> {
      if (exception == null) {
        System.out.println("Record written to offset " +
            recordMetadata.offset() + " timestamp " +
            recordMetadata.timestamp());
      } else {
        System.err.println("An error occurred");
        exception.printStackTrace(System.err);
      }
    });

    producer.flush();
    producer.close();
  }
}
