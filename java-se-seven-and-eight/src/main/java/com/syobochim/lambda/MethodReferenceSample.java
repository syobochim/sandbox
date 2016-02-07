package com.syobochim.lambda;

/**
 * @author syobochim
 */
class MethodReferenceSample {
    static void print(String text) {
        System.out.println(text);
    }
}

class MethodReferenceSample2 {
    static void print(Integer count, String text) {
        for (Integer i = 0; i < count; i++) {
            System.out.println(text);
        }
    }
}