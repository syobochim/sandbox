package com.syobochim.lambda;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author syobochim
 */
public class LambdaSampleTest {

    @Test
    public void 引数の比較() throws Exception {
        assertThat(LambdaSample.comparatorSample(2, 1) > 0, is(true));
        assertThat(LambdaSample.comparatorSample(1, 1) < 0, is(false));
    }

}