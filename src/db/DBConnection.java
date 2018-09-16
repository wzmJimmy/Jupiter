package db;

import entity.Item;
import java.util.Set;
import java.util.List;
public interface DBConnection {
	//close connection
	public void close(); 
	
	//insert favorite items by userid
	public void setFavoriteItems(String userId, List<String> itemIds); 
	//delete favorite items by userid
	public void unsetFavoriteItems(String userId, List<String> itemIds);
	//get favorite item ids by userid
	public Set<String> getFavoriteItemIds(String userId);
	//get favorite items by userid
	public Set<Item> getFavoriteItems(String userId);

	//get categories by itemid
	public Set<String> getCategories(String itemId);
	
	//search items by lat-lon and term(optional)
	public List<Item> searchItems(double lat, double lon, String term);
	//save item to database
	public void saveItem(Item item);
	
	//get full name by userid
	public String getFullname(String userId);

	//authorization
	public boolean verifyLogin(String userId, String password);
	
	//sign in
	public String signIn(String userId, String password, String first_name, String last_name);
}
