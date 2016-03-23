package com.syobochim.lambda;

import java.util.Comparator;

/**
 * @author syobochim
 */
public class CompositeFunction {

    Comparator<Person> lastNameComparator = (p1, p2) -> {
        String last1 = p1.getLast();
        String last2 = p2.getLast();

        return last1.compareTo(last2);
    };

    Comparator<Person> firstNameComparator = (p1, p2) -> {
        String first1 = p1.getFirst();
        String first2 = p2.getFirst();

        return first1.compareTo(first2);
    };

    // ある関数とある関数を組み合わせる -> 合成関数
    Comparator<Person> comparator = lastNameComparator.thenComparing(firstNameComparator);
}
