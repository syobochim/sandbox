package com.syobochim.lambda;

import org.junit.Test;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static java.awt.SystemColor.text;
import static org.junit.Assert.*;

/**
 * @author syobochim
 */
public class MethodReferenceSampleTest {
    
    @Test
    public void ラムダ式でstaticメソッドをコール() throws Exception {
        Consumer<String> consumer = text -> MethodReferenceSample.print(text);
        consumer.accept("Hello World");
    }

    @Test
    public void クラスメソッド参照でstaticメソッドをコール() throws Exception {
        Consumer<String> consumer = MethodReferenceSample::print;
        consumer.accept("Hello World");
    }

    @Test
    public void 複数の引数をとるクラスメソッド参照() throws Exception {
        // 普通に呼び出す
        BiConsumer<Integer, String> consumer1 = (count, text) -> MethodReferenceSample2.print(count, text);
        consumer1.accept(10, "Hello Workd!");

        // メソッド参照で書く
        BiConsumer<Integer, String> consumer2 = MethodReferenceSample2::print;
        consumer2.accept(10, "Hello Java");

        // ちなみに、どちらのコードもオートボクシング・オートアンボクシングしてる
    }
}