package elisa.devtest.endtoend;

import elisa.devtest.endtoend.dao.OrderDao;
import elisa.devtest.endtoend.model.Order;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

import java.io.IOException;
import java.util.Collection;

@Path("/orders")
public class OrderResource {
	OrderDao orderDao = new OrderDao();
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Order> getOrders() {
        return new OrderDao().findOrders();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void createOrder(@FormParam("product_id") String product_id,
                    @FormParam("product_name") String product_name,
                    @FormParam("quantity") Integer quantity) throws IOException {
    	orderDao.createOrderLine(product_id, product_name, quantity);
    }
    
}
