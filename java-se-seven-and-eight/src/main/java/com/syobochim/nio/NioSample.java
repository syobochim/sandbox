package com.syobochim.nio;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
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
        // Visitorを定義
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

    /**
     * ファイルツリーを走査する。
     *
     * @param path 走査対象のディレクトリ
     * @throws IOException
     */
    static void fileVisitorSample(Path path) throws IOException {
        FileVisitor<Path> visitor = new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("pre visit directory : " + dir);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println("visit file : " + file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                System.out.println("visit file failed : " + file);
                return FileVisitResult.TERMINATE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                System.out.println("post visit directory : " + dir);
                return FileVisitResult.CONTINUE;
            }
        };

        Files.walkFileTree(path, visitor);
    }

    static void deleteDir(Path path) throws IOException {
        FileVisitor<Path> visitor = new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.TERMINATE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (!dir.equals(path)) {
                    Files.delete(dir);
                }
                return FileVisitResult.CONTINUE;
            }
        };

        Files.walkFileTree(path, visitor);
    }
}
