package com.syobochim.lambda;

/**
 * @author Mizuki Wada
 */
interface Greeting {
    // デフォルトメソッドだけを定義したインターフェースも作成することが出来る
    default void sayTo(String person) {
        System.out.println("Nice to meet you, " + person + "!");
    }
}

interface Regards extends Greeting {
    // デフォルトメソッドを再抽象化
    @Override
    void sayTo(String person);
}

interface Hello extends Greeting {
    // デフォルトメソッドをオーバーライドすることも可能
    @Override
    default void sayTo(String person) {
        System.out.println("Hello, " + person + "!");
    }
}
