package dao;
/**
 * It extends the generic class AbstractDAO and inherits all the methods, but with return type Order
 * It also has some methods that have been changed such that they work for "client" field, as opposed to "name" field used in AbstractDAO superclass
 * @author Flueran Robert-Denis
 *
 */
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import connection.ConnectionFactory;
import model.Order;
import model.OrderItem;

public class OrderDAO extends AbstractDAO<Order>{
	private final Class<Order> type;

	@SuppressWarnings("unchecked")
	public OrderDAO() {
		this.type = (Class<Order>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

	}
	
	
	public Order findByName(String name) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String query = createSelectQuery("client");
		System.out.println(query);
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query);
			statement.setString(1, name);

			resultSet = statement.executeQuery();
			if(resultSet.next()) {
				resultSet.previous();
				return createObjects(resultSet).get(0);
			}
				
			return null;
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, type.getName() + "DAO:findByName " + e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet);
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
		return null;
	}
	
	protected List<Order> createObjects(ResultSet resultSet) {
		List<Order> list = new ArrayList<Order>();

		try {
			while (resultSet.next()) {
				Order instance = type.newInstance();
				for (Field field : type.getDeclaredFields()) {
					Object value = resultSet.getObject(field.getName());
					System.out.println("Field is " + value);
					PropertyDescriptor propertyDescriptor = new PropertyDescriptor(field.getName(), type);
					Method method = propertyDescriptor.getWriteMethod();
					method.invoke(instance, value);
				}
				list.add(instance);
				System.out.println(list.get(0).getClass());
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		return list;
	}
}
