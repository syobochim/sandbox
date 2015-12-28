package com.syobochim.nio;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * {@link NioSample}のテストクラス
 *
 * @author syobochim
 */
public class NioSampleTest {

    @Before
    public void setUp() throws Exception {
        // Setup
        Files.createDirectories(Paths.get("output"));
    }

    @After
    public void tearDown() throws Exception {
        // After
        Files.delete(Paths.get("output"));
    }

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

    @Test
    public void 相対パスを渡すと新規ファイルが作成される() throws Exception {
        // SetUp
        String path = "output/sample.txt";

        // Execute
        NioSample.createFile(path);

        // Verify
        assertThat(Files.readAllLines(Paths.get(path)), is(contains("hoge", "fuga")));
        Files.delete(Paths.get(path));
    }

}