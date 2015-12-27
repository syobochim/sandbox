package com.syobochim.nio;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * {@link NioSample}のテストクラス
 *
 * @author syobochim
 */
public class NioSampleTest {

    // FIXME: こういう環境依存系のテストのやり方調べる
    @Test
    public void 相対パスを渡すと絶対パスが返る() throws Exception {
        String actual = NioSample.toAbsolutePath("./input/sample.txt");
        assertThat(actual, is(allOf(startsWith("/"), endsWith("/input/sample.txt"))));
    }

    @Test
    public void 相対パスを渡すと簡略化される() throws Exception {
        String actual = NioSample.toAbsolutePath("./input/sample.txt");
        assertThat(actual, not(containsString("/./")));
    }

}