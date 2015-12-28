package com.syobochim.nio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Nioのお試し
 *
 * @author syobochim
 */
class NioSample {

    /**
     * 相対パスを絶対パスに変換する
     *
     * @param path 相対パス
     * @return 絶対パス
     */
    static String toAbsolutePath(String path) {
        Path relativePath = Paths.get(path);

        // 冗長なパスを簡潔にする
        relativePath = relativePath.normalize();

        // 相対パスを絶対パスへ変換
        return String.valueOf(relativePath.toAbsolutePath());
    }

    /**
     * ファイルを新規作成する。
     *
     * @param path ファイルパス
     * @throws IOException
     */
    static void createFile(String path) throws IOException {
        Path newFile = Paths.get(path);

        List<String> contents = new ArrayList<>();
        contents.add("hoge");
        contents.add("fuga");

        // StandardOpenOptionでファイルを開く時の開き方を指定することができる。
        Files.write(newFile, contents, StandardOpenOption.CREATE);
    }
}
