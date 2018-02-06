package com.socialpoint.ranking.service;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * General service that holds in memory {@link ConcurrentSkipListMap} for the {@link User}s
 *
 * {@link ReentrantLock} is chosen so that we don't have to synchronize the whole object or the function.
 * Although {@link ConcurrentSkipListMap} is thread safe it doesn't support the concurrency for
 * submitAbsoluteScore and submitRelativeScore operations
 * Variables fro functions left as primitives so that no null check is necessary
 * */
public class TopKRankingService {

    /**
     * The reason to use ConcurrentSkipListMap is because it supports multithreading and it is sorted by key by ranking.
     * Key and value are both user reference this way we can benefit from sorting and also we don't need to have a separate map from userId to User
     * as Set does not support get by key. Note:ceiling() and floor() can be used to search the element but it will add more complexity
     */
    protected ConcurrentSkipListMap<User, User> idToUserSortedMap = new ConcurrentSkipListMap<>((a, b)-> {
            if (a.getUserID() == b.getUserID()) {
                return 0;
            }
            return a.getRank() > b.getRank() ? -1 : 1;
    });

    /**
     * assuming total rank can be both negative and positive
     * @param userId id of the user
     * @param total absolute total score of the user
     */
    public void submitAbsoluteScore(final long userId, final long total){
        validateIfPositive(userId, "UserId");
        User user = new User(userId, total);
        idToUserSortedMap.put(user, user);
    }

    /**
     *
     * @param userId id of the user
     * @param score to submit
     */
    public void submitRelativeScore(final long userId, final long score){
        validateIfPositive(userId, "UserId");
        User user = new User(userId,score);
        User toUpsertUser = Optional.ofNullable(idToUserSortedMap.get(user)).flatMap(u -> {
            User updateUser = new User(userId, u.getRank()+score);
            return Optional.of(updateUser);
        }).orElse(user);
        idToUserSortedMap.put(toUpsertUser, toUpsertUser);
    }

    /**
     * @param topK
     * @return list of absolute list of topK users
     */
    public List<User> getTopKAbsoluteRanking(final int topK){
        validateIfPositive(topK, "TopK");
        int limit = Math.min(idToUserSortedMap.size(), topK);
        return idToUserSortedMap.entrySet().stream().limit(limit).map(Map.Entry::getValue).collect(Collectors.toList());
    }

    /**
     * Not so efficient as we will have to iterate each times
     * time complexity is O(n), space complexity is O(relativity)
     * @param topK
     * @param relativity
     * @return list of relative users
     */
    public List<User> getTopKRelativeRanking(final int topK, final int relativity){
        validateIfPositive(topK, "TopK");
        validateIfNonNegative(relativity, "Relativity");
        Iterator<Map.Entry<User, User>> iterator = idToUserSortedMap.entrySet().iterator();
        int startIndex = Math.max(1, topK-relativity);
        int forwardStepCount = calculateForwardStepCount(relativity, startIndex, topK);
        List<User> result = new ArrayList<>();
        while (iterator.hasNext() && startIndex > 1) {
            startIndex--;
            iterator.next();
        }
        while (iterator.hasNext() && forwardStepCount --> 0) {
            result.add(iterator.next().getKey());
        }
        return result;
    }

    private int calculateForwardStepCount(final int relativity, final int startIndex, final int topK) {
        return startIndex +
               relativity +
               (topK > 1 ? 1 : 0); // Zero if the topK is at the beginning
    }

    private void validateIfPositive(long parameter, String parameterName) {
        if (parameter < 1) {
            throw new IllegalArgumentException(String.format("%s must be positive!",parameterName));
        }
    }

    private void validateIfNonNegative(long parameter, String parameterName) {
        if (parameter < 0) {
            throw new IllegalArgumentException(String.format("%s must not be lower than zero!",parameterName));
        }
    }

}

