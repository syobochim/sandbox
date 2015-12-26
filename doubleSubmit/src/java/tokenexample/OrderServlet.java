package tokenexample;
 
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
 
@WebServlet(name = "OrderServlet", urlPatterns = {"/order"})
public class OrderServlet extends HttpServlet {
    private static final Map<UUID, CompletableFuture<Order>> futures = new ConcurrentHashMap<>();
    
    @PersistenceContext(unitName = "example")
    private EntityManager em;
    
    @Inject
    private OrderService orderService;
    
    @Resource
    private ManagedExecutorService executor;

     // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UUID token = UUID.randomUUID();
        request.getSession().setAttribute("csrfToken", token.toString());
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet NewServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<form method=\"post\"><input type=\"hidden\" name=\"csrf_token\" value=\"" + token.toString() + "\"/>");
            out.println("<input type=\"hidden\" name=\"item_cd\" value=\"001\"/>");
            out.println("<input type=\"submit\" value=\"Buy!\"/>");
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
        }
    }
 
    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String requestedToken = request.getParameter("csrf_token");
        System.out.println("Request received. csrf_token:" + requestedToken);
        if (requestedToken == null) {
            throw new ServletException("Token not found.");
        }
        UUID token = UUID.fromString(requestedToken);
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new ServletException("Session not found.");
        }
        synchronized(session) {
            String sessionToken = (String) session.getAttribute("csrfToken");
            if (sessionToken != null) {
                if (requestedToken.equals(sessionToken)) {
                    final String itemCd = request.getParameter("item_cd");
                    futures.put(token, CompletableFuture.supplyAsync(() -> {
                        Order order = new Order();
                        order.setItemCd(itemCd);
                        order.setToken(token.toString());
                        return orderService.register(order);
                    }, executor));
                } else {
                    throw new ServletException("トークンが一致しません");                    
                }
                session.removeAttribute("csrfToken");
            }
        }
 
        CompletableFuture<Order> future = futures.get(token);
        Order order;
        if (future == null) {
            try {
                order = em.createNamedQuery("Order.findByToken", Order.class)
                    .setParameter("token", requestedToken)
                    .getSingleResult();
            } catch (NoResultException ex) {
                throw new ServletException("Invalid token", ex);
            }
        } else {
            try {
                order = future.get(30, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                throw new ServletException(ex);
            } finally {
                futures.remove(token);                
            }
        }
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException ex) {
            Logger.getLogger(OrderServlet.class.getName()).log(Level.SEVERE, "interrupted.", ex);
        }
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet NewServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Accept your order: " + order.getItemCd() + "</h1>");
            out.println("<p>order id: " + order.getId() + "</p>");
            out.println("</body>");
            out.println("</html>");
        }
    }
 
    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "token example";
    }
 
}