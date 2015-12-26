package tokenexample;
 
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
 
@Dependent
public class OrderService {
    @PersistenceContext(unitName = "example")
    private EntityManager em;
    
    @Transactional
    public Order register(Order order) {
        List<Order> exists = em.createNamedQuery("Order.findByToken", Order.class)
                .setParameter("token", order.getToken())
                .getResultList();
        if (exists.size() > 0) {
            return exists.get(0);
        } else {
            em.persist(order);
            return order;
        }
    }
}