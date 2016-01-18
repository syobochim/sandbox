package com.syobochim.nio;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
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
     *
     * @param path       ファイルパス
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

    /**
     * ディレクトリの中をサブディレクトリを含めて削除する。ただし、引数として受け取ったディレクトリ自体は削除しない。
     *
     * @param path ディレクトリ
     * @throws IOException
     */
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

    /**
     * ディレクトリ配下のファイルをサブディレクトリを含めてコピーする
     *
     * @param sourceDirectory      コピー元ディレクトリ
     * @param destinationDirectory コピー先ディレクトリ
     * @throws IOException
     */
    static void copyDir(Path sourceDirectory, Path destinationDirectory) throws IOException {

        FileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                // サブフォルダをコピー

                // resolve メソッドにてパスを解消している。
                // resolveは絶対パスは絶対パスのまま、相対パスはガッチャンコして絶対パスに変換してくれる！
                Files.copy(dir, destinationDirectory.resolve(dir), StandardCopyOption.COPY_ATTRIBUTES);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                // ファイルをコピー
                Files.copy(file, destinationDirectory.resolve(file), StandardCopyOption.COPY_ATTRIBUTES);
                return FileVisitResult.CONTINUE;
            }
        };

        Files.walkFileTree(sourceDirectory, visitor);
    }

    static void watchDir(Path targetPath) throws IOException {
        // WatchServiceはcloseしなきゃいけない
        // サブディレクトリの監視は出来ないため、サブディレクトリを参照したい場合はサブディレクトリ用のWatchServiceが必要。
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {

            // ディレクトリに対する監視対象の動作を定義する。
            targetPath.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);

            while (true) {
                // takeメソッドでイベントが発生するまでブロックする。
                // 現在発生しているkeyを取得
                try {
                    WatchKey key = watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
                            key.reset();
                            continue;
                        } else {
                            System.out.println(event.kind() + " " + event.context());
                        }

                        if (!key.reset()) {
                            break;
                        }
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }

        }
    }
}
