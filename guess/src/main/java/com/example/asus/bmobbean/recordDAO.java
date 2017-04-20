package com.example.asus.bmobbean;

import com.example.asus.greendao.entity.SingleRecord;

/**
 * Created by yinghao on 2017/1/20.
 * Email：756232212@qq.com
 */

public class recordDAO {

    public static void creatRecord(record record, String diffcult, int rightNum, int movieNum, int sumScore) {
        if (diffcult.equals("简单")) {
            record.setRsum1(rightNum);
            record.setAverage1(sumScore / movieNum);
            record.setSum1(movieNum);
            record.setSumScore1(sumScore);
        } else if (diffcult.equals("一般")) {
            record.setRsum2(rightNum);
            record.setAverage2(sumScore / movieNum);
            record.setSum2(movieNum);
            record.setSumScore2(sumScore);
        } else {
            record.setRsum3(rightNum);
            record.setAverage3(sumScore / movieNum);
            record.setSum3(movieNum);
            record.setSumScore3(sumScore);
        }
    }

    public static void updateRecord(record record, String diffcult, int rightNum, int movieNum, int sumScore) {
        if (diffcult.equals("简单")) {
            record.setRsum1(record.getRsum1() + rightNum);
            record.setSum1(record.getSum1() + movieNum);
            record.setSumScore1(record.getSumScore1() + sumScore);
            record.setAverage1(record.getSumScore1() / record.getSum1());
        } else if (diffcult.equals("一般")) {
            record.setRsum2(record.getRsum2() + rightNum);
            record.setSum2(record.getSum2() + movieNum);
            record.setSumScore2(record.getSumScore2() + sumScore);
            record.setAverage2(record.getSumScore2() / record.getSum2());
        } else {
            record.setRsum3(record.getRsum3() + rightNum);
            record.setSum3(record.getSum3() + movieNum);
            record.setSumScore3(record.getSumScore3() + sumScore);
            record.setAverage3(record.getSumScore3() / record.getSum3());
        }
    }

    public static void creatRecord(SingleRecord record, String diffcult, int rightNum, int movieNum, int sumScore) {
        if (diffcult.equals("简单")) {
            record.setRsum1(rightNum);
            record.setAverage1(sumScore / movieNum);
            record.setSum1(movieNum);
            record.setSumScore1(sumScore);
        } else if (diffcult.equals("一般")) {
            record.setRsum2(rightNum);
            record.setAverage2(sumScore / movieNum);
            record.setSum2(movieNum);
            record.setSumScore2(sumScore);
        } else {
            record.setRsum3(rightNum);
            record.setAverage3(sumScore / movieNum);
            record.setSum3(movieNum);
            record.setSumScore3(sumScore);
        }
    }

    public static void updateRecord(SingleRecord record, String diffcult, int rightNum, int movieNum, int sumScore) {
        if (diffcult.equals("简单")) {
            record.setRsum1(record.getRsum1() + rightNum);
            record.setSum1(record.getSum1() + movieNum);
            record.setSumScore1(record.getSumScore1() + sumScore);
            record.setAverage1(record.getSumScore1() / record.getSum1());
        } else if (diffcult.equals("一般")) {
            record.setRsum2(record.getRsum2() + rightNum);
            record.setSum2(record.getSum2() + movieNum);
            record.setSumScore2(record.getSumScore2() + sumScore);
            record.setAverage2(record.getSumScore2() / record.getSum2());
        } else {
            record.setRsum3(record.getRsum3() + rightNum);
            record.setSum3(record.getSum3() + movieNum);
            record.setSumScore3(record.getSumScore3() + sumScore);
            record.setAverage3(record.getSumScore3() / record.getSum3());
        }
    }
}
