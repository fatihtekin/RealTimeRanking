package com.socialpoint.ranking.service.test;

import com.socialpoint.ranking.service.TopKRankingService;
import com.socialpoint.ranking.service.User;
import com.sun.tools.javac.util.Assert;

import java.util.*;

public class TopKRankingServiceTest {

    public static void testSubmitRelative() throws Exception {
        TopKRankingService topKRankingService = getInitializedData();
        topKRankingService.submitAbsoluteScore(123, 3000);
        List<User> expectedList = Arrays.asList(new User(123,3000), new User(100,1000), new User(99,999));
        Assert.check(isTwoArrayListsWithSameValues(topKRankingService.getTopKAbsoluteRanking(3), expectedList));
    }

    public static void testSubmitAbsolute() throws Exception {
        TopKRankingService topKRankingService = getInitializedData();
        topKRankingService.submitRelativeScore(100, -10);
        topKRankingService.submitRelativeScore(100, +20);
        List<User> expectedList = Arrays.asList(new User(100,1010), new User(99,999), new User(98,998));
        Assert.check(isTwoArrayListsWithSameValues(topKRankingService.getTopKAbsoluteRanking(3),expectedList));
    }

    public static void testEmptyUsers() throws Exception {
        TopKRankingService topKRankingService = new TopKRankingService();
        Assert.check(isTwoArrayListsWithSameValues(topKRankingService.getTopKAbsoluteRanking(3), Collections.emptyList()),"");
        Assert.check(isTwoArrayListsWithSameValues(topKRankingService.getTopKRelativeRanking(1,1), Collections.emptyList()));
        Assert.check(isTwoArrayListsWithSameValues(topKRankingService.getTopKRelativeRanking(1,0), Collections.emptyList()));
        Exception error = null;
        try{
            Assert.check(isTwoArrayListsWithSameValues(topKRankingService.getTopKRelativeRanking(0,0), Collections.emptyList()));
        }catch (Exception e){
            error = e;
        }
        Assert.check(error instanceof IllegalArgumentException,"Should be IllegalArgumentException when topK is lower than 1");
        Assert.checkNonNull(error,"Should not be null");
    }

    public static void testTop1With0RelativeRanking() throws Exception {
        TopKRankingService topKRankingService = getInitializedData();
        List<User> expectedList = Arrays.asList(new User(100, 1000));
        Assert.check(isTwoArrayListsWithSameValues(topKRankingService.getTopKRelativeRanking(1, 0), expectedList));
    }

    public static void testTop1With1RelativeRanking() throws Exception {
        TopKRankingService topKRankingService = getInitializedData();
        List<User> expectedList = Arrays.asList(new User(100, 1000),new User(99, 999));
        Assert.check(isTwoArrayListsWithSameValues(topKRankingService.getTopKRelativeRanking(1, 1), expectedList));
    }

    public static void testLeftAndRightHandSideLargeEnoughRelativeRanking() throws Exception {
        TopKRankingService topKRankingService = getInitializedData();
        List<User> expectedList = Arrays.asList(new User(99, 999), new User(98, 998),
                new User(97, 997), new User(96, 996), new User(95, 995));
        Assert.check(isTwoArrayListsWithSameValues(topKRankingService.getTopKRelativeRanking(4, 2), expectedList));
    }

    public static void testLeftHandSideLowerRelativeRanking() throws Exception {
        TopKRankingService topKRankingService = getInitializedData();
        List<User> expectedList = Arrays.asList(new User(100, 1000), new User(99, 999),
                                                new User(98, 998), new User(97, 997));
        Assert.check(isTwoArrayListsWithSameValues(topKRankingService.getTopKRelativeRanking(2, 2), expectedList));
    }

    public static void testRightHandSideLowerRelativeRanking() throws Exception {
        TopKRankingService topKRankingService = getInitializedData();
        List<User> expectedList = Arrays.asList(new User(5, 905), new User(4, 904), new User(3, 903),
                new User(2, 902), new User(1, 901));
        Assert.check(isTwoArrayListsWithSameValues(topKRankingService.getTopKRelativeRanking(99, 3), expectedList));
    }

    public static void testAbsoluteTopLowerCountTest(){
        TopKRankingService topKRankingService = new TopKRankingService();
        topKRankingService.submitAbsoluteScore(1,100);
        List<User> expectedList = Arrays.asList(new User(1, 100));
        Assert.check(isTwoArrayListsWithSameValues(topKRankingService.getTopKAbsoluteRanking(10), expectedList));
    }

    public static void testAbsoluteTopKTest(){
        TopKRankingService topKRankingService = new TopKRankingService();
        topKRankingService.submitAbsoluteScore(1,100);
        topKRankingService.submitAbsoluteScore(2,200);
        topKRankingService.submitAbsoluteScore(3,300);
        topKRankingService.submitAbsoluteScore(4,400);
        topKRankingService.submitAbsoluteScore(5,500);
        List<User> expectedList = Arrays.asList(new User(5, 500), new User(4, 400), new User(3, 300));
        Assert.check(isTwoArrayListsWithSameValues(topKRankingService.getTopKAbsoluteRanking(3), expectedList));
        expectedList = new ArrayList<>(expectedList);
        expectedList.add(new User(1, 100));
        expectedList.add(new User(2, 200));
        Assert.check(isTwoArrayListsWithSameValues(topKRankingService.getTopKAbsoluteRanking(5), expectedList));
    }

    public static void main(String[] args) throws Exception{
        testSubmitRelative();
        testSubmitAbsolute();
        testEmptyUsers();
        testTop1With0RelativeRanking();
        testTop1With1RelativeRanking();
        testLeftAndRightHandSideLargeEnoughRelativeRanking();
        testLeftHandSideLowerRelativeRanking();
        testRightHandSideLowerRelativeRanking();
        testAbsoluteTopLowerCountTest();
        testAbsoluteTopKTest();
    }

    private static TopKRankingService getInitializedData(){
        TopKRankingService topKRankingService = new TopKRankingService();
        int idCounter = 100;
        int scoreCounter = 1000;
        List<User> list = new ArrayList<>();
        while (idCounter>0) {
            list.add(new User(idCounter, scoreCounter));
            idCounter--;
            scoreCounter--;
        }
        Random rand = new Random();
        while (!list.isEmpty()) {
            int randomIndex = rand.nextInt(list.size());
            User randomUser = list.get(randomIndex);
            list.remove(randomUser);
            topKRankingService.submitAbsoluteScore(randomUser.getUserID(),randomUser.getRank());
        }
        return topKRankingService;
    }

    public static boolean isTwoArrayListsWithSameValues(List<User> list1, List<User> list2) {
        if(list1==null && list2==null)
            return true;
        if((list1 == null && list2 != null) || (list1 != null && list2 == null))
            return false;
        if(list1.size()!=list2.size())
            return false;
        for (Object itemList1 : list1) {
            if (!list2.contains(itemList1))
                return false;
        }
        return true;
    }
}