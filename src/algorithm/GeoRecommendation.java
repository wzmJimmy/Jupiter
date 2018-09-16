package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.Map.Entry;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

public class GeoRecommendation {

	 public List<Item> recommendItems(String userId, double lat, double lon) {
		List<Item> recommends = new ArrayList<Item>();
		DBConnection conn = DBConnectionFactory.getConnection();
		/*get favorite ids*/
		Set<String> favorite_ids = conn.getFavoriteItemIds(userId);
		/*summary category imformation*/
		Map<String, Integer> allCategories = new HashMap<>();
		for(String item_id : favorite_ids) {
			Set<String> categories = conn.getCategories(item_id);
			for(String category:categories) 
				{allCategories.put(category, allCategories.getOrDefault(category, 0)+1);}
		}
		//sort by size of the category
		List<Entry<String, Integer>> categoryList = new ArrayList<>(allCategories.entrySet());
		Collections.sort(categoryList, (Entry<String, Integer> o1, Entry<String, Integer> o2) 
				-> {return Integer.compare(o2.getValue(), o1.getValue());}
				);	
		/*search,filter, and sort events*/
		Set<Item> visitedItems = new HashSet<>();
		
		for (Entry<String, Integer> category : categoryList) {
			List<Item> items = conn.searchItems(lat, lon, category.getKey());
			List<Item> filteredItems = new ArrayList<>();
			
			for (Item item : items) {
				if (!favorite_ids.contains(item.getItemId()) && !visitedItems.contains(item)) {
					filteredItems.add(item);
				}
			}
			
			Collections.sort(filteredItems, (Item item1, Item item2) 
					-> {return Double.compare(item1.getDistance(), item2.getDistance());}
					);
			
			visitedItems.addAll(items);
			recommends.addAll(filteredItems);
		}
		conn.close();
		return recommends;
	 }
}
