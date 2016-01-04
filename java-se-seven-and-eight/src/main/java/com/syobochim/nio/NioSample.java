package com.syobochim.nio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    static void createFile(Path path) throws IOException {
        List<String> contents = new ArrayList<>();
        contents.add("hoge");
        contents.add("fuga");

        // StandardOpenOptionでファイルを開く時の開き方を指定することができる。
        Files.write(path, contents, StandardOpenOption.CREATE);
    }

    /**
     * ファイルのパーミッション情報を取得する
     *
     * @param path ファイルパス
     */
    static void changePermission(Path path, String permission) throws IOException {
        Set<PosixFilePermission> posixFilePermissions = PosixFilePermissions.fromString(permission);
        Files.setPosixFilePermissions(path, posixFilePermissions);
    }

    /**
     * ファイルのパーミッションを指定してファイルを作成する。
     * @param path ファイルパス
     * @param permission 権限
     * @throws IOException
     */
    static void createFile(Path path, String permission) throws IOException {
        Set<PosixFilePermission> posixFilePermissions = PosixFilePermissions.fromString(permission);
        FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(posixFilePermissions);
        Files.createFile(path, attr);
    }

}
