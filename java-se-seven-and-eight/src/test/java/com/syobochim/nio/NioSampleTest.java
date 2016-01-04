package com.syobochim.nio;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

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
        Path path = Paths.get("output/sample.txt");

        // Execute
        NioSample.createFile(path);

        // Verify
        assertThat(Files.readAllLines(path), is(contains("hoge", "fuga")));
        Files.delete(path);
    }

    @Test
    public void ファイルの権限を変更する() throws Exception {
        Path path = Paths.get("output/permission-check.txt");
        Files.createFile(path);
        Set<PosixFilePermission> posixFilePermissions = Files.getPosixFilePermissions(path);
        assertThat(PosixFilePermissions.toString(posixFilePermissions), is("rw-r--r--"));

        NioSample.changePermission(path, "rwxrwxrw-");

        Set<PosixFilePermission> actual = Files.getPosixFilePermissions(path);
        assertThat(PosixFilePermissions.toString(actual), is("rwxrwxrw-"));
        Files.delete(path);
    }

    @Test
    public void ファイル権限を指定してファイルを生成する() throws Exception {
        Path path = Paths.get("output/new-file.txt");
        NioSample.createFile(path, "rwx------");

        Set<PosixFilePermission> actual = Files.getPosixFilePermissions(path);
        assertThat(PosixFilePermissions.toString(actual), is("rwx------"));
        Files.delete(path);
    }

    @Test
    public void デイlレクトリを走査する() throws Exception {
        NioSample.createFile(Paths.get("output/new-file.txt"));
        NioSample.createFile(Paths.get("output/sample.txt"));
        Files.createDirectory(Paths.get("output/sub"));
        NioSample.createFile(Paths.get("output/sub/sample.txt"));
        Files.createDirectory(Paths.get("output/sub/sub2"));
        NioSample.createFile(Paths.get("output/sub/sub2/sample.txt"));
        NioSample.createFile(Paths.get("output/sub/sub2/sample2.txt"));

        Path path = Paths.get("output");
        NioSample.fileVisitorSample(path);

        NioSample.deleteDir(path);

    }

}