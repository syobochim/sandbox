package com.syobochim.lambda;

import java.util.Comparator;

/**
 * @author syobochim
 */
public class LambdaSample {

    /**
     * 引数を比較する。
     *
     * @param x 引数1
     * @param y 引数2
     * @return 引数1が引数2より大きい場合は正数が返る
     */
    public static int comparatorSample(int x, int y) {
        Comparator<Integer> comparator = Integer::compare;
        return comparator.compare(x, y);
    }

}
