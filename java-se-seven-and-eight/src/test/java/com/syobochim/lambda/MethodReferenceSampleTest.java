package com.syobochim.lambda;

import org.junit.Test;

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
}