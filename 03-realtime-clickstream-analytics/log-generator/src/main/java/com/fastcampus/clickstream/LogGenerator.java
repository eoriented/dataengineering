package com.fastcampus.clickstream;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class LogGenerator implements Runnable {
    private CountDownLatch latch;
    private String ipAddr;
    private String sessionId;
    private int durationSeconds;
    private Random rand;

    private final static long MINIMUM_SLEEP_TIME = 500;
    private final static long MAXIMUM_SLEEP_TIME = 60 * 1000;

    private final static String TOPIC_NAME = "weblog";

    public LogGenerator(CountDownLatch latch, String ipAddr, String sessionId, int durationSeconds) {
        this.latch = latch;
        this.ipAddr = ipAddr;
        this.sessionId = sessionId;
        this.durationSeconds = durationSeconds;
        this.rand = new Random();
    }

    @Override
    public void run() {
        System.out.println("Starting log generator (ipAddr=" + ipAddr + ", sessionId=" + sessionId + ", durationSeconds=" + durationSeconds);

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "LogGenerator");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        long startTime = System.currentTimeMillis();

        while (isDuration(startTime)) {
            long sleepTime = MINIMUM_SLEEP_TIME + Double.valueOf(rand.nextDouble() * (MAXIMUM_SLEEP_TIME - MINIMUM_SLEEP_TIME)).longValue();
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String responseCode = getResponseCode();
            String responseTime = getResponseTime();
            String method = getMethod();
            String url = getUrl();
            OffsetDateTime offsetDataTime = OffsetDateTime.now(ZoneId.of("UTC"));

            String log = String.format("%s %s %s %s %s %s %s",
                    ipAddr, offsetDataTime, method, url, responseCode, responseTime, sessionId);
            System.out.println(log);

            producer.send(new ProducerRecord<>(TOPIC_NAME, log));
        }
        producer.close();

        System.out.println("Stopping log generator (ipAddr=" + ipAddr + ", sessionId=" + sessionId + ", durationSeconds=" + durationSeconds);

        this.latch.countDown();
    }

    private boolean isDuration(long startTime) {
        return System.currentTimeMillis() - startTime < durationSeconds * 1000L;
    }

    private String getResponseCode() {
        String responseCode = "200";
        if (rand.nextDouble() > 0.97) {
            responseCode = "404";
        }
        return responseCode;
    }

    private String getResponseTime() {
        int responseTime = 100 + rand.nextInt(901);
        return String.valueOf(responseTime);
    }

    private String getMethod() {
        if (rand.nextDouble() > 0.7) {
            return "POST";
        } else {
            return "GET";
        }
    }

    private String getUrl() {
        double randomValue = rand.nextDouble();
        if (randomValue > 0.9) {
            return "/ads/page";
        } else if (randomValue > 0.8) {
            return "/doc/page";
        } else {
            return "/";
        }
    }
}
