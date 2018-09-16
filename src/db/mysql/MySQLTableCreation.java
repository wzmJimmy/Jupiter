package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class MySQLTableCreation {
	// Run this as Java application to reset db schema.
		public static void main(String[] args) {
			try {
				// Step 1 Connect to MySQL.
				System.out.println("Connecting to " + MySQLDBUtil.URL);
				Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
				Connection conn = DriverManager.getConnection(MySQLDBUtil.URL);
				
				if (conn == null) {return;}
				Statement stmt = conn.createStatement();
				/*delete*/
				stmt.executeUpdate("DROP TABLE IF EXISTS histories");
				stmt.executeUpdate("DROP TABLE IF EXISTS categories");
				stmt.executeUpdate("DROP TABLE IF EXISTS users");
				stmt.executeUpdate("DROP TABLE IF EXISTS items");
				/*create*/
				stmt.executeUpdate("CREATE TABLE users("
						+ "user_id varchar(255) NOT NULL,password varchar(255),"
						+ "first_name varchar(255),last_name varchar(255),"
						+ "PRIMARY KEY(user_id))");
				stmt.executeUpdate("CREATE TABLE items("
						+ "item_id varchar(255) NOT NULL,name varchar(255),"
						+ "rating float,address varchar(255),image_url varchar(255),"
						+ "url varchar(255),distance float,"
						+ "PRIMARY KEY(item_id))");
				stmt.executeUpdate("CREATE TABLE histories("
						+ "user_id varchar(255) NOT NULL,item_id varchar(255) NOT NULL,"
						+ "last_favor_time timestamp,"
						+ "PRIMARY KEY(user_id,item_id),"
						+ "FOREIGN KEY(user_id) REFERENCES users(user_id),"
						+ "FOREIGN KEY(item_id) REFERENCES items(item_id))");
				stmt.executeUpdate("CREATE TABLE categories("
						+ "category varchar(255) NOT NULL,item_id varchar(255) NOT NULL,"
						+ "PRIMARY KEY(category,item_id),"
						+ "FOREIGN KEY(item_id) REFERENCES items(item_id))");
				/*insert*/
				stmt.executeUpdate("INSERT INTO users()"
						//+ "(user_id,password,first_name,last_name)"
						+ "VALUES('0001','8d73173c25b3bbb66ee0f1a5ed6e5db8','Jimmy','Wang')");
				
				System.out.println("Import done successfully");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

}
