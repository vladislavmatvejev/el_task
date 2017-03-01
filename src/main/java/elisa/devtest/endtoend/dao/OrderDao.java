package elisa.devtest.endtoend.dao;

import elisa.devtest.endtoend.model.Customer;
import elisa.devtest.endtoend.model.Order;
import elisa.devtest.endtoend.model.OrderLine;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class OrderDao {

    public List<Order> findOrders() {
        try {
            return createJdbcTemplate().query("select * from orders", (resultSet, rowNumber) -> new Order(resultSet.getLong("order_id"), findCustomer(resultSet.getLong("customer_id")), findOrderLines(resultSet.getLong("order_id"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }

    private List<OrderLine> findOrderLines(long orderId) {
        try {
            return createJdbcTemplate().query("select * from order_line where order_id = ?", new Object[]{orderId}, (resultSet, rowNumber) -> new OrderLine(resultSet.getLong("order_line_id"), resultSet.getString("product_id"), resultSet.getString("product_name"), resultSet.getInt("quantity")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }

    public Customer findCustomer(final long customerId) {
        try {
            return createJdbcTemplate().queryForObject("select * from customer where customer_id = ?", new Object[]{customerId}, (resultSet, rowNumber) -> new Customer(resultSet.getLong("customer_id"), resultSet.getString("company_name"), resultSet.getString("street"), resultSet.getString("postal_code"), resultSet.getString("city"), resultSet.getString("country")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Customer();
    }
    
    //create order
    public long createOrder() {
    	try {
    		long order_id = getLastId("orders", "order_id") + 1;
    		String sql = "INSERT INTO orders VALUES(? , 1)";
    		PreparedStatement statement = createJdbcTemplate().getDataSource().getConnection().prepareStatement(sql);
    		statement.setLong(1, order_id);
    		statement.execute();
    		return order_id;
    	} catch (Exception e) {
    		e.printStackTrace();
    		return 0;
    	}
    }
    //create order line
    public void createOrderLine(String product_id, String product_name, Integer quantity){
    	try {
    		long order_id = createOrder();
    		long order_line_id = getLastId("order_line", "order_line_id") + 1;
    		String sql = "INSERT INTO order_line (order_line_id, order_id, product_id, product_name, quantity) VALUES (?, ?, ?, ?, ?)";
    		PreparedStatement statement = createJdbcTemplate().getDataSource().getConnection().prepareStatement(sql);
    		statement.setLong(1, order_line_id);
    		statement.setLong(2, order_id);
    		statement.setString(3, product_id);
    		statement.setString(4, product_name);
    		statement.setInt(5, quantity);
    		statement.execute();
    	}catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    //get last id of existing entities
	public long getLastId(String table, String field) throws EmptyResultDataAccessException{
    	try {
        	return createJdbcTemplate().queryForObject("SELECT "+field+" FROM (SELECT * FROM "+table+" ORDER BY "+field+" desc) WHERE rownum = 1", (resultSet, rowNumber) ->  resultSet.getLong(field));
    	} catch (EmptyResultDataAccessException e) {
    		return 0;
    	}
    }

    private JdbcTemplate createJdbcTemplate() {
        return new JdbcTemplate(DBConnection.getDataSource());
    }

}
