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

interface Hi extends Greeting {
    @Override
    default void sayTo(String person){
        System.out.println("Hi," + person + "!");
    }
}

class Hey implements Greeting {
    @Override
    public void sayTo(String person) {
        System.out.println("Hey, " + person + "!");
    }
}

// 同じシグネチャのデフォルトメソッドを継承しているのでコンパイルエラー
//class CasualGreeting implements Hello, Hi {}

// 必ずオーバーライドする
class CasualGreeting implements Hello, Hi {
    @Override
    public void sayTo(String person) {
        System.out.println("Good Day, " + person + "!");
    }
}

// 実装とインターフェースでは、実装が優先される。
class SampleGreeting extends Hey implements Hello {}

class SuperGreeting implements Hello, Hi {
    @Override
    public void sayTo(String person) {
        Hello.super.sayTo(person);
    }
}