package com.socialpoint.ranking.service.test;
import com.socialpoint.ranking.service.ConcurrentTopKRankingService;
import com.socialpoint.ranking.service.TopKRankingService;
import com.sun.tools.javac.util.Assert;

import java.util.*;
import java.util.concurrent.*;

public class ConcurrentTopKRankingServiceTest {

    public static void testConcurrency() throws Exception {

        TopKRankingService concurrentTopKRankingService = new ConcurrentTopKRankingService();
        final int initialCapacity = 1000000;
        List<Integer> randomRelativeSubmits = new ArrayList<Integer>(initialCapacity);
        int count = initialCapacity;
        while(count > 0){
            randomRelativeSubmits.add(count);
            count--;
        }
        long seed = System.nanoTime();
        Collections.shuffle(randomRelativeSubmits, new Random(seed));
        ConcurrentLinkedQueue<Integer> concurrentLinkedQueue = new ConcurrentLinkedQueue();
        concurrentLinkedQueue.addAll(randomRelativeSubmits);
        ExecutorService pool = Executors.newFixedThreadPool(1000);
        Runnable supplier = () -> concurrentTopKRankingService.submitRelativeScore(1, concurrentLinkedQueue.poll());
        count = initialCapacity;
        List<Future<Void>> results = new ArrayList<>();
        while(count > 0){
            results.add(CompletableFuture.runAsync(supplier, pool));
            count--;
        }
        for (Future<Void> completableFuture : results) {
            completableFuture.get();
        }
        pool.shutdown();
        pool.awaitTermination(5000, TimeUnit.SECONDS);
        long result = concurrentTopKRankingService.getTopKAbsoluteRanking(1).get(0).getRank();
        long expected = ((long) initialCapacity * ((long) initialCapacity + 1)) / 2;
        Assert.check(expected == result, String.format("Expected %s but result is %s",expected,result));

    }

    public static void main(String[] args) throws Exception {
        testConcurrency();
    }
}
