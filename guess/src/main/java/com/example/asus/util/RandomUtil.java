package com.example.asus.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Ahab on 2016/10/22.
 */
public class RandomUtil {

    //获取n个不重复的随机数，范围为0~M 不包括M
    public static List<Integer> getRandomNums(int n, int m) {
        Random random = new Random();
        List<Integer> randomNums = new ArrayList<>();
        for (; randomNums.size() < n; ) {
            int temp = random.nextInt(m);
            if (!randomNums.contains(temp)) {
                randomNums.add(temp);
            }
        }
        return randomNums;
    }

    //获取n个可以重复的随机数，范围为0~M 不包括M
    public static List<Integer> getRepeatRandomNums(int n, int m) {
        Random random = new Random();
        List<Integer> randomNums = new ArrayList<>();
        for (; randomNums.size() < n; ) {
            int temp = random.nextInt(m);
            randomNums.add(temp);
        }
        return randomNums;
    }

    //获取1个可以重复的随机数，范围为0~M 不包括M
    public static int get1RandomNums(int m) {
        return new Random().nextInt(m);
    }



    /**
     * 对key集合填充一个或两个key中不存在的字符
     *
     * @param chars
     * @return
     */
    public static List<Character> add1or2char(List<Character> chars) {
        List<Character> newChars = new ArrayList<>();
        newChars.addAll(chars);
        char newChar1;
        do {
            newChar1 = (char) (RandomUtil.get1RandomNums(26) + 'A');
        } while (newChars.contains(newChar1));
        newChars.add(newChar1);
        int oneORtwo = RandomUtil.get1RandomNums(2);
        if (oneORtwo == 1) {
            do {
                newChar1 = (char) (RandomUtil.get1RandomNums(26) + 'A');
            } while (newChars.contains(newChar1));
            newChars.add(newChar1);
        }
        return newChars;
    }
}
