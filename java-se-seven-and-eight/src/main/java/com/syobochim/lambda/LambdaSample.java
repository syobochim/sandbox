package com.syobochim.lambda;

import java.util.Comparator;
import java.util.function.IntUnaryOperator;

/**
 * @author syobochim
 */
class LambdaSample {

    /**
     * 引数を比較する。
     *
     * @param x 引数1
     * @param y 引数2
     * @return 引数1が引数2より大きい場合は正数が返る
     */
    static int comparatorSample(int x, int y) {
        Comparator<Integer> comparator = Integer::compare;
        return comparator.compare(x, y);
    }

    public static void finalSample() {
        int y = 2;
        y = 4;
        // final でない変数を使用することはできない。
//        IntUnaryOperator func = x -> x * y;
    }

}
