# 関数型インターフェース

## 基本となる関数型インターフェース

インターフェースはたくさんあるが、基本となるインターフェースは以下の通り。

|インターフェース名|メソッド名|説明|
|:-------------:|:--------:|:------:|
|Function<T, R>|R apply(T t)|引数あり、返り値あり|
|Consumer<T>|void accept(T t)|引数あり、返り値なし|
|Predicate<T>|boolean test(T t)|引数あり、返り値boolean|
|Supplier<T>|T get()|引数なし、返り値あり|

また、int / long / double の3つの型に対応した関数型インターフェースもある。

* IntFunction
* DoubleFunction

Functionインターフェース系のサブインターフェースとして以下が提供されている。

|インターフェース名|スーパーインターフェース名|メソッド名|説明|
|:--------------:|:--------------------:|:-----:|:----|
|UnaryOperator<T>|Function<T, T>|T apply(T t)|引数と返り値が同じ|
|BinaryOperator<T>|BiFunction<T, T, T>|T apply(T t1, T t2)|引数と返り値が同じ。引数は二つ|

引数を二つにしたインターフェースはインターフェース名の先頭にBiがつく。

## 関数型インターフェースの作成

自分で関数型インターフェースを作成することもできる。実装するメソッドが１つのインターフェースを作成すればいい。
以下のようにアノテーションをつけることで、インターフェースが関数型インターフェースかチェックすることができる。アノテーションは必須ではない。

```java
@FunctionalInterface
public interface TriFunction<S, T, U, R> {
    R apply(S s, T t, U u);
}
```


