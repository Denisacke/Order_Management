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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import connection.ConnectionFactory;
import model.Order;

/**
 * It contains all functions and queries required for CRUD operations. All functions are generic and can be used with each one of the four database tables
 * @author Flueran Robert-Denis
 * 
 */
public class AbstractDAO<T> {
	protected static final Logger LOGGER = Logger.getLogger(AbstractDAO.class.getName());

	private final Class<T> type;

	@SuppressWarnings("unchecked")
	public AbstractDAO() {
		this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

	}

	protected String createSelectQuery(String field) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ");
		sb.append("* ");
		sb.append("FROM ");
		sb.append("`" + type.getSimpleName() + "`");
		sb.append(" WHERE " + field + " =?");
		return sb.toString();
	}

	private String createSelectAll() {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ");
		sb.append("* ");
		sb.append("FROM ");
		sb.append("`" + type.getSimpleName() + "`");
		return sb.toString();
	}
	
	private String createInsertQuery(T t) {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO ");
		sb.append("`" + type.getSimpleName() + "`(");
		for (int i = 0; i < t.getClass().getDeclaredFields().length; i++) {
			t.getClass().getDeclaredFields()[i].setAccessible(true); 
			try {
				if(i < t.getClass().getDeclaredFields().length - 1)
					sb.append(t.getClass().getDeclaredFields()[i].getName() + ",");
				else
					sb.append(t.getClass().getDeclaredFields()[i].getName());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}

		}
		sb.append(") VALUES (");
		for (Field field : t.getClass().getDeclaredFields()) {
			field.setAccessible(true); 
			Object value;
			try {
				value = field.get(t);
				sb.append("'" + value + "',");
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

		}
		sb.setLength(sb.length() - 1);
		sb.append(")");
		return sb.toString();
	}
	private String createDeleteQuery(T t) {
		StringBuilder sb = new StringBuilder();
		sb.append("DELETE ");
		sb.append("FROM ");
		sb.append("`" + type.getSimpleName() + "`");
		sb.append(" WHERE ");
		Boolean passedFirstArg = false;
		for (Field field : t.getClass().getDeclaredFields()) {
			field.setAccessible(true); 
			Object value;
			Integer nul = 0;
			Double nul2 = 0.0;
			try {
				value = field.get(t);
				if(passedFirstArg == false) {
					passedFirstArg = true;
					continue;
				}
				else {
					
					if(value != null && !(value.equals(nul)) && !(value.equals(nul2)))
						sb.append(field.getName() + "='" + value + "' AND ");
				}
				
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

		}
		sb.setLength(sb.length() - 4);
		return sb.toString();
	}
	private String createUpdateQuery(T t) {
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE ");
		sb.append("`" + type.getSimpleName() + "`");
		sb.append(" SET ");
		Boolean passedFirstArg = false;
		for (Field field : t.getClass().getDeclaredFields()) {
			field.setAccessible(true); 
			Object value;
			try {
				value = field.get(t);
				if(passedFirstArg == false) {
					passedFirstArg = true;
					continue;
				}
				else {
					sb.append(field.getName() + "='" + value + "',");
				}
				
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

		}
		sb.setLength(sb.length() - 1);
		sb.append(" WHERE ");
		int count = 0;
		for (Field field : t.getClass().getDeclaredFields()) {
			field.setAccessible(true); 
			Object value;
			try {
				value = field.get(t);
				if(count == 0) {
					count++;
				}
				else if(count == 1){
					sb.append(field.getName() + "='" + value + "'");
					count++;
				}
				else
					break;
				
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	public List<T> findAll() {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String query = createSelectAll();
		System.out.println(query);
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query);
			resultSet = statement.executeQuery();
			return createObjects(resultSet);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, type.getName() + "DAO:findAll " + e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet);
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
		return null;
	}

	public LinkedList<String> getTableFields(T t) {

		LinkedList<String> list = new LinkedList<String>();
		for (Field field : t.getClass().getDeclaredFields()) {
			field.setAccessible(true); 
			try {
				list.add(field.getName());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} 
		}
		return list;
	}
	
	public LinkedList<Object> retrieveProperties(Object object) {

		LinkedList<Object> list = new LinkedList<Object>();
		for (Field field : object.getClass().getDeclaredFields()) {
			field.setAccessible(true); 
			Object value;
			try {
				value = field.get(object);
				list.add(value);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

		}
		return list;
	}
	
	public T findById(int id) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String query = createSelectQuery("id");
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query);
			statement.setInt(1, id);
			resultSet = statement.executeQuery();

			return createObjects(resultSet).get(0);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, type.getName() + "DAO:findById " + e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet);
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
		return null;
	}

	public T findByName(String name) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String query = createSelectQuery("name");
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
	
	@SuppressWarnings("deprecation")
	private List<T> createObjects(ResultSet resultSet) {
		List<T> list = new ArrayList<T>();

		try {
			while (resultSet.next()) {
				T instance = type.newInstance();
				for (Field field : type.getDeclaredFields()) {
					Object value = resultSet.getObject(field.getName());
					PropertyDescriptor propertyDescriptor = new PropertyDescriptor(field.getName(), type);
					Method method = propertyDescriptor.getWriteMethod();
					method.invoke(instance, value);
				}
				list.add(instance);
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

	public T insert(T t) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String query = createInsertQuery(t);
		System.out.println(query);
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			statement.executeUpdate();
			ResultSet rs = statement.getGeneratedKeys();
			if (rs.next()) {
				System.out.println("Got to insert " + t.getClass());
				return t;
			}
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, type.getName() + "DAO:Insert " + e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet);
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
		return null;
	}

	public T update(T t) {
		Connection connection = null;
		PreparedStatement statement = null;
		String query = createUpdateQuery(t);
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			statement.executeUpdate(query);
			System.out.println(query);
			return t;
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, type.getName() + "DAO:Delete " + e.getMessage());
		} finally {
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
		return null;
	}
	
	public T delete(T t) {
		Connection connection = null;
		PreparedStatement statement = null;
		String query = createDeleteQuery(t);
		System.out.println(query);
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			statement.executeUpdate(query);
			return t;
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, type.getName() + "DAO:Delete " + e.getMessage());
		} finally {
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
		return null;
	}
}
