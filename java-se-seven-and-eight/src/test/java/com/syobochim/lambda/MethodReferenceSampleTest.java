package com.syobochim.lambda;

import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

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

    @Test
    public void オブジェクトを指定できる場合のインスタンスメソッド参照() throws Exception {
        // ラムダ式からの呼び出し
        // 実質的finalでなければならない。
        List<String> texts = new ArrayList<>();
        Consumer<String> consumer1 = text -> texts.add(text);
        consumer1.accept("A");

        // メソッド参照
        Consumer<String> consumer2 = texts::add;
        consumer2.accept("B");

        // 実質的finalではなくてもメソッド参照なら引数として使用できる
        List<String> texts2 = new ArrayList<>();
        texts2 = new LinkedList<>();
        consumer2.accept("C");
    }

    @Test
    public void オブジェクトを指摘できない場合のインスタンスメソッド参照() throws Exception {
        // ラムダ式からの呼び出し
        Function<String, String> func1 = text -> text.trim();
        String trimmedText1 = func1.apply(" Hello World!");
        System.out.println(trimmedText1);

        // メソッド参照からの呼び出し
        // オブジェクトを指定できないので、Stringというクラス名を記述する。
        Function<String, String> func2 = String::trim;
        String trimmedText2 = func2.apply("  Hello Java  ");
        System.out.println(trimmedText2);

        // 引数のある場合
        // ラムダ式から
        BiFunction<String, String, String> func3 = (text, concatText) -> text.concat(concatText);
        String result1 = func3.apply("Hello, ", "World");
        System.out.println(result1);

        // メソッド参照
        // 引数を前から順に適用してくれる!!
        BiFunction<String, String, String> func4 = String::concat;
        String result2 = func4.apply("Hello, ", "Java");
        System.out.println(result2);
    }
}