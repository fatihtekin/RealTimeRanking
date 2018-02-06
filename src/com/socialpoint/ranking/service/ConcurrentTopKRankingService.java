package com.socialpoint.ranking.service;

import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * General service that holds in memory {@link ConcurrentSkipListMap} for the {@link User}s
 *
 * {@link ReentrantLock} is chosen so that we don't have to synchronize the whole object or the function.
 * Although {@link ConcurrentSkipListMap} is thread safe we do still need to support the concurrency for
 * submitAbsoluteScore and submitRelativeScore operations as multiple threads can call for the same user and do the update
 * and this would have result in the latter operation to override the former operation
 */
public class ConcurrentTopKRankingService extends TopKRankingService{

    private final ReentrantLock lock = new ReentrantLock();

    /**
     * assuming total rank can be both negative and positive
     * @param userId id of the user
     * @param total absolute total score of the user
     */
    @Override
    public void submitAbsoluteScore(final long userId, final long total){
        try {
            lock.tryLock(5, TimeUnit.SECONDS);
        }catch (Exception e){
            throw new IllegalStateException("Could not submitAbsoluteScore",e);
        }
        super.submitAbsoluteScore(userId, total);
        lock.unlock();
    }

    /**
     *
     * @param userId id of the user
     * @param score to submit
     */
    @Override
    public void submitRelativeScore(final long userId, final long score){
        try {
            lock.tryLock(5, TimeUnit.SECONDS);
        }catch (Exception e){
            throw new IllegalStateException("Could not submitRelativeScore",e);
        }
        super.submitRelativeScore(userId, score);
        lock.unlock();
    }

}

