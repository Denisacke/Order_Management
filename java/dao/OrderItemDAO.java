package dao;

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
import model.OrderItem;
/**
 * It extends the generic class AbstractDAO and inherits all the methods, but with return type OrderItem
 * It also has some methods that have been changed such that they work for "client" field, as opposed to "name" field used in AbstractDAO superclass
 * @author Flueran Robert-Denis
 *
 */
public class OrderItemDAO extends AbstractDAO<OrderItem>{

	private final Class<OrderItem> type;

	@SuppressWarnings("unchecked")
	public OrderItemDAO() {
		this.type = (Class<OrderItem>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

	}
	
	public OrderItem findByName(String name) {
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
			//System.out.println("String is " + resultSet.get);
			if(resultSet.next()) {
				resultSet.previous();
				System.out.println("THIS");
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
	
	public List<OrderItem> findAllByName(String name) {
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
			//System.out.println("String is " + resultSet.get);
			if(resultSet.next()) {
				resultSet.previous();
				System.out.println("THIS");
				return createObjects(resultSet);
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
	
	
	protected List<OrderItem> createObjects(ResultSet resultSet) {
		List<OrderItem> list = new ArrayList<OrderItem>();

		try {
			while (resultSet.next()) {
				OrderItem instance = type.newInstance();
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
