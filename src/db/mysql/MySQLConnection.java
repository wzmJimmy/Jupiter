package db.mysql;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import db.DBConnection;
import entity.Item;
import entity.Item.ItemBuilder;
import external.TicketMasterAPI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLConnection implements DBConnection {
	
	private Connection conn;
	
	public MySQLConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
			conn = DriverManager.getConnection(MySQLDBUtil.URL);	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	private boolean isNull(Connection conn) {
		if(conn == null) {System.err.println("DBconnection Failed!");return true;}
		return false;
	}

	@Override
	public void close() {
		if(conn!=null) {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void setFavoriteItems(String userId, List<String> itemIds) {
		if(isNull(conn)) return;
		String sql = "INSERT IGNORE INTO histories(user_id, item_id) VALUES (?, ?)";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
	   		 for (String itemId : itemIds) {
	   			 ps.setString(2, itemId);
	   			 ps.execute();
	   		 }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		 
	}

	@Override
	public void unsetFavoriteItems(String userId, List<String> itemIds) {
		if(isNull(conn)) return;
		String sql = "DELETE FROM histories WHERE user_id = ? AND item_id = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
	   		 for (String itemId : itemIds) {
	   			 ps.setString(2, itemId);
	   			 ps.execute();
	   		 }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public Set<String> getFavoriteItemIds(String userId) {
		if(isNull(conn)) return new HashSet<String>();
		
		String sql = "SELECT item_id AS id FROM histories WHERE user_id = ?";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);
			ResultSet rs = stmt.executeQuery();
			Set<String> item_ids = new HashSet<String>();
			while (rs.next()) {
				item_ids.add(rs.getString("id"));
			}
			return item_ids;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new HashSet<String>();
	}

	@Override
	public Set<Item> getFavoriteItems(String userId) {
		if(isNull(conn)) return new HashSet<Item>();

		Set<Item> items = new HashSet<Item>();
		Set<String> itemIds = getFavoriteItemIds(userId);
		String sql = "SELECT * FROM items WHERE item_id = ?";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			for (String id : itemIds) {
				stmt.setString(1, id);
				ResultSet rs = stmt.executeQuery();
				ItemBuilder builder = new ItemBuilder();
				while (rs.next()) { // start from "-1", end with "null"
					builder.setItemId(id);
					builder.setName(rs.getString("name"));
					builder.setAddress(rs.getString("address"));
					builder.setImageUrl(rs.getString("image_url"));
					builder.setUrl(rs.getString("url"));
					builder.setCategories(getCategories(id));
					builder.setDistance(rs.getDouble("distance"));
					builder.setRating(rs.getDouble("rating"));
					items.add(builder.build());
				}
			}
			return items;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new HashSet<Item>();
	}

	@Override
	public Set<String> getCategories(String itemId) {
		if (isNull(conn)) return new HashSet<String>();
		String sql = "SELECT category FROM categories WHERE item_id = ?";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, itemId);
			ResultSet rs = stmt.executeQuery();
			Set<String> categories = new HashSet<String>();
			while (rs.next()) {
				categories.add(rs.getString("category"));
			}
			return categories;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new HashSet<String>();
	}

	@Override
	public List<Item> searchItems(double lat, double lon, String term) {
		List<Item> items = TicketMasterAPI.search(lat, lon, term);
		for (Item item : items) {
			saveItem(item);
		}
		return items;
	}

	@Override
	public void saveItem(Item item) {
		if(isNull(conn)) return;
		//ignore: ignore duplicate primary-key insert.
		String sql = "INSERT IGNORE INTO items VALUES (?,?,?,?,?,?,?)";
		String sql2 = "INSERT IGNORE INTO categories(item_id,category) VALUES (?,?)";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, item.getItemId());
			statement.setString(2, item.getName());
			statement.setDouble(3, item.getRating());
			statement.setString(4, item.getAddress());
			statement.setString(5, item.getImageUrl());
			statement.setString(6, item.getUrl());
			statement.setDouble(7, item.getDistance());
			statement.execute();
			
			PreparedStatement statement2 = conn.prepareStatement(sql2);
			statement2.setString(1, item.getItemId());
			for(String category : item.getCategories()) {
	   			 statement2.setString(2, category);
	   			 statement2.execute();
	   		 }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String getFullname(String userId) {
		if(isNull(conn)) return"";
		String name = "";
		String sql = "SELECT first_name, last_name FROM users WHERE user_id = ?";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				name = String.join("", rs.getString("first_name"),rs.getString("last_name"));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return name;
	}

	@Override
	public boolean verifyLogin(String userId, String password) {
		if(isNull(conn)) return false;
		try {
			String sql = "SELECT password FROM users WHERE user_id = ? AND password = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);
			stmt.setString(2, password);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) { return true;}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public String signIn(String userId, String password, String first_name, String last_name) {
		if(isNull(conn)) return "";
		if(userId.isEmpty()|| password.isEmpty()|| first_name.isEmpty() || last_name.isEmpty() ) return "Null";
		try {
			String sql = "SELECT user_id FROM users WHERE user_id = ?";
			PreparedStatement stmt_exist = conn.prepareStatement(sql);
			stmt_exist.setString(1, userId);
			System.out.println(stmt_exist.toString());
			ResultSet rs = stmt_exist.executeQuery();
			while (rs.next()) { return "Exist";}

			String sql2 = "INSERT INTO users(user_id,password,first_name,last_name) VALUES(?, ?, ?, ?)";
			PreparedStatement stmt = conn.prepareStatement(sql2);
			stmt.setString(1, userId);
			stmt.setString(2, password);
			stmt.setString(3, first_name);
			stmt.setString(4, last_name);
			System.out.println(stmt.toString());
			if (stmt.executeUpdate() == 0) {
	            throw new SQLException("Creating user failed, no rows affected.");
	        }
			return "Success";
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "";
	}

}
