import org.junit.Test;

import java.awt.event.ItemEvent;
import java.io.File;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author syobochim
 */
public class Lambda {

    @Test
    public void スレッドの確認() throws Exception {
        // Arrays.sortメソッド内で呼び出されるコンパレーターのコードはsortメソッドを呼び出したスレッドで実行されるのか
        System.out.println("main thread name = " + Thread.currentThread().getName());

        String[] strings = {"string", "sort", "length"};
        Arrays.sort(strings, new LengthComparator());

        System.out.println("main thread name = " + Thread.currentThread().getName());
    }

    private class LengthComparator implements Comparator<String> {
        public int compare(String first, String second) {
            // スレッド名を出力する
            System.out.println("comparator thread name = " + Thread.currentThread().getName());
            return Integer.compare(first.length(), second.length());
        }
    }

    @Test
    public void すべてのディレクトリを返す() throws Exception {
        // java.io.FileクラスのlistFilesメソッドとisDirectoryメソッドを使用して
        // 指定されたディレクトリのしたのすべてのサブディレクトリを返すメソッドを書く

        File files = new File("./");
        List<File> directoryList = createSubDirectoryList(files);
        directoryList.forEach(System.out::println);
    }

    private List<File> createSubDirectoryList(File... files) {
        List<File> directories = new ArrayList<>();
        Arrays.stream(files).forEach(file -> {
            // listFiles(FileFilter)に対してメソッド参照を使用
            File[] subDirectory = file.listFiles(File::isDirectory);
            // 再帰的に探索を実行する
            directories.addAll(createSubDirectoryList(subDirectory));
            assert subDirectory != null;
            directories.addAll(Arrays.asList(subDirectory));
        });
        return directories;
    }

    @Test
    public void 指定した拡張子のファイルを返す() throws Exception {
        // java.io.Fileクラスのlist(FilenameFilter)を使用して
        // 指定された拡張子をもつ、全てのファイルを返すメソッドを書く
        // エンクロージングスコープからキャプチャされる変数はどれですか。

        File currentDirectory = new File("./");
        String ext = "java";
        List<File> subDirectoryList = createSubDirectoryList(currentDirectory);

        List<String> fileNames = new ArrayList<>();
        subDirectoryList.forEach(file -> {
            // extはラムダ式の外の変数。エンクロージングスコープからキャプチャされた変数。
            Collections.addAll(fileNames, file.list((dir, name) -> name.endsWith("." + ext)));
        });
        fileNames.forEach(System.out::println);
    }

    @Test
    public void ファイルとディレクトリのソート() throws Exception {
        // Fileオブジェクトの配列をソートして、ファイルの前にディレクトリがくるようにする。
        // ファイルとディレクトリのそれぞれのグループではパス名でソートされるようにする。

        // 事前準備
        File currentDirectory = new File("./");
        File[] files = currentDirectory.listFiles();
        assert files != null;

        Arrays.sort(files, (File x, File y) -> {
            // 種類が同じ場合は名前で比較する
            if (x.isDirectory() & y.isDirectory() || x.isFile() & y.isFile()) {
                return x.getName().compareTo(y.getName());
            }
            // ディレクトリを優先する
            return x.isDirectory() ? -1 : 1;
        });
        Arrays.stream(files).forEach(System.out::println);
    }

    @Test
    public void インターフェースの使用をラムダ式で書き換えてみる() throws Exception {
        // インターフェースを普通に使う
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("run method");
            }
        }).start();

        // ラムダ式
        new Thread(() -> {
            System.out.println("lambda method");
        }).start();
    }

    @Test
    public void チェックされる全ての例外を非チェック例外に変える() throws Exception {
        // Runnableないでチェックされる全ての例外をキャッチし、それをチェックされない例外に帰るuncheckメソッドを書く
        new Thread(unCheck(() -> {
            System.out.println("zzz");
            Thread.sleep(1000);
        })).start();

        // Callableを使用すると戻り値がない、と言われてしまう
//        new Thread(unCheck2(() -> {
//            System.out.println("zzz");
//            Thread.sleep(1000);
//        })).start();
    }

    private static Runnable unCheck(RunnableEx runner) {
        return () -> {
            try {
                runner.run();
            } catch (Exception e) {
                new RuntimeException(e);
            }
        };
    }

    interface RunnableEx {
        void run() throws Exception;
    }

    private static Runnable unCheck2(Callable callable) {
        return () -> {
            try {
                callable.call();
            } catch (Exception e) {
                new RuntimeException(e);
            }
        };
    }

    @Test
    public void staticメソッドのandThenをかく() throws Exception {
        // andThenメソッドは2つのRunnableインスタンスをパラメータとして受け取るようにし、
        // 最初のRunnableを実行したあとに2つめのRunnableを実行するRunnableを返すようにする。
        andThen(() -> System.out.println("first"), () -> System.out.println("second")).run();
    }

    private static Runnable andThen(Runnable a, Runnable b) {
        return () -> {
            a.run();
            b.run();
        };
    }

    @Test
    public void 拡張forループ() throws Exception {
        String[] names = {"Peter", "Paul", "Mary"};
        List<Runnable> runnable = new ArrayList<>();
        for (String name : names) {
            runnable.add(() -> System.out.println(name));
        }
        System.out.println("forEach loop");
        runnable.forEach(Runnable::run);

        List<Runnable> runnable2 = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            // ラムダ式に渡すために変化しない値に書き換える
            int j = i;
            runnable2.add(() -> System.out.println(names[j]));
        }
        System.out.println("for loop");
        runnable2.forEach(Runnable::run);
    }

    @Test
    public void forEachIfを追加する() throws Exception {
        // CollectionのサブインターフェースであるCollection2を作成して、デフォルトメソッドを追加する。
        // filterがtrueを返してきたここの要素に対してactionを適用する。
        // どのような場面でそのメソッドを活用出来るでしょうか。
        String[] names = {"Peter", "Paul", "Mary"};
        Collection2<String> collection2 = new ArrayList2();
        collection2.addAll(Arrays.asList(names));
        collection2.forEachIf(System.out::println, item -> item.startsWith("P"));
    }

    interface Collection2<T> extends Collection<T> {
        // デフォルトメソッドを実装する
        default void forEachIf(Consumer<T> action, Predicate<T> filter) {
            stream().forEach(i -> {
                if (filter.test(i)) {
                    action.accept(i);
                }
            });
        }
    }

    private class ArrayList2 extends ArrayList implements Collection2 {
    }

    @Test
    public void aa() throws Exception {
        // void f()メソッドをもつIとJの2つのインターフェースがあり、両方を実装しているクラスがある。
        // Iインターフェースのfメソッドが抽象、デフォルト、staticのどれかであり、
        // Jインターフェースのfメソッドが抽象、デフォルト、staticのどれかである場合、
        // すべての組み合わせで何が起きるでしょう。
        // 同じようにスーパークラスSを拡張し、Iインターフェースを実装した場合に、
        // スーパークラスもインターフェースもvoid f()メソッドを持っていたらどうなるか。

        // 実装パターン(interface同士)
        // - 抽象メソッド同士
        // - デフォルトメソッド同士
        // - staticメソッド同士
        // - 抽象メソッドとデフォルトメソッド
        // - デフォルトメソッドとstaticメソッド
        // - staticメソッドと抽象メソッド

        // - 抽象メソッド同士
        // 一つのfメソッドを実装するよう要求される
//        interface I {abstract void f();}
//        interface J {abstract void f();}
//        class Sample implements I, J {
//            @Override
//            public void f() {
//            }
//        }

        // - デフォルトメソッド同士
        // 一つのfメソッドを実装するように要求される
        // インターフェースのデフォルトメソッドをつかうことも出来る
//        interface I {default void f(){System.out.println("Interface I");};}
//        interface J {default void f(){System.out.println("Interface J");};}
//        class Sample implements I, J {
//            @Override
//            public void f() {
//                I.super.f();
//            }
//        }

        // - staticメソッド同士
        // 問題ない。呼び出すときはインターフェースをつかう。(Sampleクラスのstaticメソッドとしては使えない)
//        I.f();
//        J.f();
//        interface I {static void f(){System.out.println("Interface I");};}
//        interface J {static void f(){System.out.println("Interface J");};}
//        class Sample implements I, J {}

        // - 抽象メソッドとデフォルトメソッド
        // どちらの順序でもOverrideを求められる
//        interface I {abstract void f();}
//        interface J {default void f(){System.out.println("Interface J");};}
//        class Sample implements I, J {
//            @Override
//            public void f() {
//            }
//        }

//        interface I {abstract void f();}
//        interface J {default void f(){System.out.println("Interface J");};}
//        class Sample implements J,I {
//            @Override
//            public void f() {
//            }
//        }

        // - デフォルトメソッドとstaticメソッド
        // どちらの順序でもSampleクラスの実装は求められない
//        interface I {static void f(){System.out.println("Interface I");};}
//        interface J {default void f(){System.out.println("Interface J");};}
//        class Sample implements I, J {}

//        interface I {static void f(){System.out.println("Interface I");};}
//        interface J {default void f(){System.out.println("Interface J");};}
//        class Sample implements J, I {}

        // - staticメソッドと抽象メソッド
        // staticメソッドが先に宣言されたときはクラスの実装が必要
        // 実装しない場合には"Lambda.Sampleはabstractでなく、Lambda.J内のabstractメソッドf()をオーバーライドしません"コンパイルエラーが出力される
//        interface I {static void f(){System.out.println("Interface I");};}
//        interface J {abstract void f();}
//        class Sample implements I, J {
//            @Override
//            public void f() {
//            }
//        }
        // abstractメソッドが先に宣言されたときはクラスの実装が不要
//        interface I {static void f(){System.out.println("Interface I");};}
//        interface J {abstract void f();}
//        class Sample implements J,I {}
    }

}

