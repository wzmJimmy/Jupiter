package rpc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import entity.Item;
import entity.Item.ItemBuilder;

public class RpcHelperTest {

//	@Test
//	public void test() {
//		fail("Not yet implemented");
//	}
	
	@Test
	public void testGetJSONArray() throws JSONException {
		Set<String> category = new HashSet<String>();
		category.add("category one");
		
		
		JSONArray jsonArray = new JSONArray();
		List<Item> listItem = new ArrayList<Item>();
		JSONAssert.assertEquals(jsonArray, RpcHelper.getJSONArray(listItem), true);
		
		Item one = new ItemBuilder().setItemId("one").setAddress("439 ECM,53703")
				.setRating(5).setCategories(category).build();
		Item two = new ItemBuilder().setItemId("two").setAddress("439 ECM,55503")
				.setRating(5).setCategories(category).build();
		//List<Item> listItem = new ArrayList<Item>();
		listItem.add(one);
		listItem.add(two);
		jsonArray.put(one.toJSONObject());
		jsonArray.put(two.toJSONObject());
		JSONAssert.assertEquals(jsonArray, RpcHelper.getJSONArray(listItem), true);
	
		Item empty = new ItemBuilder().build();
		listItem.add(empty);
		jsonArray.put(empty.toJSONObject());
		JSONAssert.assertEquals(jsonArray, RpcHelper.getJSONArray(listItem), true);
	
	}


}
