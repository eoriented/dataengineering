package com.fastcampus.clickstream;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class Main {
    static int userNum = 15;
    static int durationSeconds = 300;
    static Set<String> ipSet = new HashSet<>();
    static Random rand = new Random();

    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(userNum);
        ExecutorService executor = Executors.newFixedThreadPool(userNum);
        IntStream.range(0, userNum).forEach(i -> {
            String ipAddr = getIpAddr();
            executor.execute(new LogGenerator(latch, ipAddr, UUID.randomUUID().toString(), durationSeconds));
        });
        executor.shutdown();

        try {
            latch.await();
        } catch (InterruptedException e) {
            System.err.println(e);
        }
    }

    private static String getIpAddr() {
        while (true) {
            String ipAddr = "192.168.0." + rand.nextInt(256);
            if (!ipSet.contains(ipAddr)) {
                ipSet.add(ipAddr);
                return ipAddr;
            }
        }
    }
}
