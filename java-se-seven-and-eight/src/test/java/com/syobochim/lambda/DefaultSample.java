package com.syobochim.lambda;

import org.junit.Test;

/**
 * @author Mizuki Wada
 */
public class DefaultSample {

    @Test
    public void デフォルトメソッドの呼び出し() throws Exception {
        // インターフェースをインスタンスとして生成することが出来る
        Greeting greeting = new Greeting() {};
        // 実装をかかなくても処理がよびだされる。
        greeting.sayTo("Java");

        // デフォルトメソッドを再抽象化した場合はメソッドの実装が必要
        Greeting greeting2 = new Regards() {
            @Override
            public void sayTo(String person) {
                System.out.println("How do you do, " + person + "?");
            }
        };
        greeting2.sayTo("Java");

        // ちなみにラムダで書くとこう
        Greeting greeting3 = (Regards) person -> System.out.println("How do you do, " + person + "?");
        greeting2.sayTo("Java");

        // オーバーライドしたメソッドは通常のデフォルトメソッドをもつインターフェースと同様に使用出来る
        Greeting greeting4 = new Hello() {};
        greeting4.sayTo("Java");
    }

}
