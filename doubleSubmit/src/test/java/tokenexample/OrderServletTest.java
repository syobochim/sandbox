package tokenexample;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import javax.enterprise.concurrent.ManagedExecutorService;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author irof
 */
public class OrderServletTest {

    @InjectMocks
    OrderServlet sut;

    @Mock
    OrderService service;

    @Mock
    EntityManager em;
    @Mock(answer = Answers.RETURNS_SELF)
    TypedQuery<Order> typedQuery;

    @Mock
    HttpServletRequest request;
    HttpSession session;

    @Mock
    HttpServletResponse response;
    StringWriter out;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() throws Exception {
        // 検索をMockするためにがんばる子。
        // TypedQueryはFluentAPIなので、自身を返すMockにしておく
        // 検索結果をスタブしたくなるのは個々のテストでまちまちなので、Beforeでは準備だけ。
        when(em.createNamedQuery("Order.findByToken", Order.class))
                .thenReturn(typedQuery);

        // ManagedExecutorServiceを正常に動かすための少々強引な実装。
        // ProxyでExecutorServiceのインスタンスに流し込む。
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        ManagedExecutorService executor = (ManagedExecutorService) Proxy.newProxyInstance(
                this.getClass().getClassLoader(),
                new Class[]{ManagedExecutorService.class},
                (p, m, a) -> m.invoke(executorService, a));
        // @Mockじゃないから@InjectMocksで突っ込んでくれない。仕方ないのでリフレクション。
        Field field = OrderServlet.class.getDeclaredField("executor");
        field.setAccessible(true);
        field.set(sut, executor);

        // HttpSessionを期待どおり動かすための少々強引な実装。
        // attributeの操作が動作に影響するので単純なMockだと厳しいので、
        // 最低限の動作としてMapに委譲するProxyで対応する。
        // 実際はこんな実装せず、Spring-testのMockHttpSessionとかを使えば良い。
        HashMap<Object, Object> map = new HashMap<>();
        session = (HttpSession) Proxy.newProxyInstance(
                this.getClass().getClassLoader(),
                new Class[]{HttpSession.class},
                (p, m, a) -> {
                    switch (m.getName()) {
                        case "setAttribute":
                            map.put(a[0], a[1]);
                            return null;
                        case "getAttribute":
                            return map.get(a[0]);
                        case "removeAttribute":
                            map.remove(a[0]);
                            return null;
                    }
                    throw new UnsupportedOperationException(m.getName());
                }
        );
        when(request.getSession(false)).thenReturn(session);

        // 処理結果はresponseから取得されるPrintWriterに書き込まれる。
        // PrintWriter#println をMockしてverifyでもいいが、それだと実装依存すぎる。
        // 例えば println を別のメソッドに置き換えた時などに落ちる脆いテストになる。
        // そのため、出力結果をStringで取得できるStringWriterを渡し、これをアサートする。
        out = new StringWriter();
        PrintWriter printWriter = new PrintWriter(out);
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    public void 登録するぜ() throws Exception {
        // sessionとrequestのTokenが一致する必要がある
        String token = UUID.randomUUID().toString();
        session.setAttribute("csrfToken", token);
        when(request.getParameter("csrf_token")).thenReturn(token);

        // 登録されるOrderのアイテムコード
        when(request.getParameter("item_cd")).thenReturn("TEST-ITEM-CODE-HOGE");
        // サービスは引数のOrderにIDを設定して返す子にしておく。
        // 「サービスを呼ばないとこんな事にならない」感じの動作をさせるほうが良い。
        when(service.register(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = (Order) invocation.getArguments()[0];
                    order.setId(111111L);
                    return order;
                });

        sut.doPost(request, response);

        // このアサーションでサービスが呼ばれていることはわかる。
        assertThat(out.toString())
                .contains("Accept your order: TEST-ITEM-CODE-HOGE")
                .contains("order id: 111111");

        // ----- 以下は非推奨の検証 -----
        // 以下のような「呼び出しが無いこと」の検証は本質ではないのでなるべく避ける。
        verify(typedQuery, never()).getSingleResult();
        // また、以下のように「呼び出しがあったこと」を検証したくなるかもしれないが、
        // レスポンスに含まれるidなどでわかるならそちらに寄せたほうが良い。
        // なぜなら「サービスが呼ばれた」だけではこのServletとしては物足りず、
        // 正常にレスポンスが返っているところまで見届けたい。
        // アサーションを複数行っても良いのだけれど、複数のアサーションは
        // 脆いテストの兆候なので、可能な限り避けたい。
        verify(service).register(argThat(obj -> {
            Order order = (Order) obj;
            return order.getItemCd().equals("TEST-ITEM-CODE-HOGE")
                    && order.getToken().equals(token);
        }));
    }

    @Test
    public void セッションにトークンがなかったら登録せず検索するぜ() throws Exception {
        when(request.getParameter("csrf_token"))
                .thenReturn(UUID.randomUUID().toString());
        // 引数のtokenに一致する場合に返されるOrder
        // ......と言うのをMockで表そうとすると実装ガチ依存になるのでしない。
        // この辺りはFluentAPIなものをMockする時に多少諦める範囲かもしれない。
        Order order = new Order();
        order.setId(222222L);
        order.setItemCd("TEST-ITEM-CODE-FUGA");
        when(typedQuery.getSingleResult()).thenReturn(order);

        sut.doPost(request, response);

        // このアサーションで検索が行われていることはわかる。
        assertThat(out.toString())
                .contains("Accept your order: TEST-ITEM-CODE-FUGA")
                .contains("order id: 222222");

        // ----- 以下は非推奨の検証 -----
        verify(service, never()).register(any(Order.class));
        verify(typedQuery).getSingleResult();
    }

    @Test
    public void パラメーターにトークンがなかったら動かないぜ() throws Exception {
        thrown.expect(ServletException.class);
        thrown.expectMessage("Token not found.");

        when(request.getParameter("csrf_token"))
                .thenReturn(null);
        sut.doPost(request, response);
    }

    @Test
    public void sessionがなかったら動かないぜ() throws Exception {
        thrown.expect(ServletException.class);
        thrown.expectMessage("Session not found.");

        // リクエストパラメーターでトークンは飛んできた
        when(request.getParameter("csrf_token"))
                .thenReturn(UUID.randomUUID().toString());
        // けど、セッションが無効だった...
        when(request.getSession(false))
                .thenReturn(null);
        sut.doPost(request, response);
    }

    @Test
    public void トークンが不一致だと動かないぜ() throws Exception {
        // 「登録ボタン押した直後に同じセッションで登録画面を開く」とかで起こる。
        thrown.expect(ServletException.class);
        thrown.expectMessage("トークンが一致しません");

        // リクエストパラメーターでトークンは飛んできた
        when(request.getParameter("csrf_token"))
                .thenReturn(UUID.randomUUID().toString());
        // けど、セッションには入っているトークンは別のだった...
        session.setAttribute("csrfToken", UUID.randomUUID().toString());
        sut.doPost(request, response);
    }

    @Test
    public void トークンに一致するレコードがないと例外だぜ() throws Exception {
        // 「二重サブミット」の間にレコードが削除されたり、一度目の登録が例外吐いたりで起こる。
        thrown.expect(ServletException.class);
        thrown.expectMessage("Invalid token");

        // リクエストパラメーターでトークンは飛んできた
        when(request.getParameter("csrf_token"))
                .thenReturn(UUID.randomUUID().toString());
        // セッションにトークンは入っていない
        assertThat(session.getAttribute("csrfToken")).isNull();
        // この状態だと登録されているものとして検索を行う。
        // 同じトークンのレコードが登録されていると期待するが、
        // 検索したらレコード無しの例外が発生した。
        when(typedQuery.getSingleResult())
                .thenThrow(NoResultException.class);

        sut.doPost(request, response);
    }

    @Test
    public void カバレッジ稼ぎにdoGet通しとくぜ() throws Exception {
        when(request.getSession()).thenReturn(session);
        sut.doGet(request, response);
    }

    @Test
    public void カバレッジ稼ぎにdoServletInfo通しとくぜ() throws Exception {
        sut.getServletInfo();
    }
}