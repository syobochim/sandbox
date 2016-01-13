package com.syobochim.lambda;

import java.util.Comparator;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;

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

    /**
     * 実質的finalについて
     */
    public static void finalSample() {
        int y = 2;
        y = 4;
        // final でない変数を使用することはできない。
//        IntUnaryOperator func = x -> x * y;

        final int z = 3;
        // finalな変数は使用できる
        IntUnaryOperator func2 = x -> x * z;

        // finalな変数への再代入はできない
/*        IntUnaryOperator func3 = x -> {
            z += 10;
            return x * z;
        }*/

        // 再代入されていないローカル変数をfinalとして扱うことができる。（実質的final）
        int w = 3;
        IntUnaryOperator func = x -> x * z;
    }

    public LambdaSample() {
        // 匿名クラスにてthisを使う。thisは匿名クラス自体を示す
        Supplier<Void> func1 = new Supplier<Void>() {
            @Override
            public Void get() {
                System.out.println("Task1 class : " + this.getClass()); // Task1 class : class com.syobochim.lambda.LambdaSample$1
                return null;
            }
        };

        // ラムダ式にてthisを使う。thisはクラス自体を示す
        Supplier<Void> func2 = () -> {
            System.out.println("Task2 class : " + this.getClass()); // Task2 class : class com.syobochim.lambda.LambdaSample
            return null;
        };

        func1.get();
        func2.get();
    }

}
