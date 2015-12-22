package tokenexample;
 
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
 
@Entity
@Table(name = "orders")
@NamedQueries(@NamedQuery(
        name = "Order.findByToken",
        query = "SELECT o FROM Order o WHERE o.token=:token"))
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String itemCd;
    private String token;
 
    public Long getId() {
        return id;
    }
 
    public void setId(Long id) {
        this.id = id;
    }
 
    public String getItemCd() {
        return itemCd;
    }
 
    public void setItemCd(String itemCd) {
        this.itemCd = itemCd;
    }
 
    public String getToken() {
        return token;
    }
 
    public void setToken(String token) {
        this.token = token;
    }
 }