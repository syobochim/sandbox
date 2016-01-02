package tokenexample;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author irof
 */
public class OrderServiceTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @InjectMocks
    OrderService sut = new OrderService();

    @Mock
    EntityManager em;
    @Mock(answer = Answers.RETURNS_SELF)
    TypedQuery typedQuery;

    @Before
    public void setup() {
        when(em.createNamedQuery(any(String.class), any(Class.class)))
                .thenReturn(typedQuery);
    }

    @Test
    public void 検索してとれなかったら登録して返すよ() throws Exception {
        when(typedQuery.getResultList())
                .thenReturn(Collections.emptyList());

        Order order = new Order();
        Order actual = sut.register(order);

        assertThat(actual).isSameAs(order);
        verify(em).persist(order);
    }

    @Test
    public void 検索してとれたらそれを返すよ() throws Exception {
        when(typedQuery.getResultList())
                .thenReturn(Collections.singletonList(new Order()));

        Order order = new Order();
        Order actual = sut.register(order);

        assertThat(actual).isNotSameAs(order);
    }
}