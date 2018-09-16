package external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
import entity.Item.ItemBuilder;


public class TicketMasterAPI {
	private static final String URL = "https://app.ticketmaster.com/discovery/v2/events.json";
	private static final String DEFAULT_KEYWORD = ""; // no restriction
	private static final String API_KEY = "930dVtsZKgtAMJqf8vziGPaJmgnilKec";
	
	private static final String EMBEDDED = "_embedded";
	private static final String EVENTS = "events";
	private static final String NAME = "name";
	private static final String ID = "id";
	private static final String URL_STR = "url";
	private static final String RATING = "rating";
	private static final String DISTANCE = "distance";
	private static final String VENUES = "venues";
	private static final String ADDRESS = "address";
	private static final String LINE1 = "line1";
	private static final String LINE2 = "line2";
	private static final String LINE3 = "line3";
	private static final String CITY = "city";
	private static final String IMAGES = "images";
	private static final String CLASSIFICATIONS = "classifications";
	private static final String SEGMENT = "segment";
	
	/**
	 * Helper methods
	 */
	
	private static List<Item> getItemList(JSONArray events) throws JSONException{
		List<Item> itemList = new ArrayList<>();
		for (int i = 0; i < events.length(); ++i) {
			JSONObject event = events.getJSONObject(i);
			
			ItemBuilder builder = new ItemBuilder();
			
			if (!event.isNull(NAME)) {
				builder.setName(event.getString(NAME));
			}
			if (!event.isNull(ID)) {
				builder.setItemId(event.getString(ID));
			}
			if (!event.isNull(URL_STR)) {
				builder.setUrl(event.getString(URL_STR));
			}
			if (!event.isNull(RATING)) {
				builder.setRating(event.getDouble(RATING));
			}
			if (!event.isNull(DISTANCE)) {
				builder.setDistance(event.getDouble(DISTANCE));
			}
			
			builder.setAddress(getAddress(event));
			builder.setCategories(getCategories(event));
			builder.setImageUrl(getImageUrl(event));
			
			itemList.add(builder.build());
		}

		return itemList;
		
	}

	private static String getAddress(JSONObject event) throws JSONException {
		if (event.isNull(EMBEDDED)) return null;
		JSONObject embedded = event.getJSONObject(EMBEDDED);
		if (embedded.isNull(VENUES)) return null;
		JSONArray venues = embedded.getJSONArray(VENUES);
		for (int i = 0; i < venues.length(); ++i) {
			JSONObject venue = venues.getJSONObject(i);
			
			StringBuilder sb = new StringBuilder();
			if (!venue.isNull(ADDRESS)) {
				JSONObject address = venue.getJSONObject(ADDRESS);
				
				if (!address.isNull(LINE1)) {
					sb.append(address.getString(LINE1));
				}
				if (!address.isNull(LINE2)) {
					sb.append('\n');
					sb.append(address.getString(LINE2));
				}
				if (!address.isNull(LINE3)) {
					sb.append('\n');
					sb.append(address.getString(LINE3));
				}
			}
			
			if (!venue.isNull(CITY)) {
				JSONObject city = venue.getJSONObject(CITY);
				
				if (!city.isNull(NAME)) {
					sb.append('\n');
					sb.append(city.getString(NAME));
				}
			}
			
			if (!venue.isNull("state")) {
				JSONObject state = venue.getJSONObject("state");
				
				if (!state.isNull("stateCode")) {
					sb.append("\n");
					sb.append(state.getString("stateCode"));
				}
			}if (!venue.isNull("postalCode")) {
				sb.append('-');
				sb.append(venue.getString("postalCode"));
			}
			
			String addr = sb.toString();
			if (!addr.equals("")) {
				return addr;
			}
		}
		return "";
		
	}
	
	// {"images": [{"url": "www.example.com/my_image.jpg"}, ...]}
	private static String getImageUrl(JSONObject event) throws JSONException {
		if (event.isNull(IMAGES)) return null;
		JSONArray images = event.getJSONArray(IMAGES);
		for(int i = 0; i<images.length();i++) {
			JSONObject image = images.getJSONObject(i);
			if(!image.isNull(URL_STR)) {
				return image.getString(URL_STR);
			}
		}
		return "";
	}

	// {"classifications" : [{"segment": {"name": "music"}}, ...]}
	private static Set<String> getCategories(JSONObject event) throws JSONException {
		Set<String> categories = new HashSet<>();
		if (event.isNull(CLASSIFICATIONS)) return categories;
		JSONArray classifications = event.getJSONArray(CLASSIFICATIONS);
		
		for (int i = 0; i < classifications.length(); ++i) {
			JSONObject classification = classifications.getJSONObject(i);
			
			if (!classification.isNull(SEGMENT)) {
				JSONObject segment = classification.getJSONObject(SEGMENT);
				if (!segment.isNull(NAME)) {
					categories.add(segment.getString(NAME));
				}
			}
		}
		return categories;
	}




	public static List<Item> search(double lat,double lon,String keyword) {
		/*first encode keyword.*/
		if(keyword==null) {keyword = DEFAULT_KEYWORD;}
		try {
			keyword = URLEncoder.encode(keyword,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		/*combine and get connection based on url.*/
		String geohash = GeoHash.encodeGeohash(lat, lon, 8);
		int radius = 50;
		//Double radius = 50.0;
		String query = String.format("apikey=%s&geoPint=%s&keyword=%s&radius=%d", 
				API_KEY,geohash,keyword,radius);
//		String query = String.format("apikey=%s&postalCode=%s&keyword=%s&radius=%d", 
//				API_KEY,"53703",keyword,radius);
		try {
			/*processing request and respond.*/
			HttpURLConnection connection = (HttpURLConnection) new URL(URL+"?"+query).openConnection();
			connection.setRequestMethod("GET");
			// Real action: code is status code, respond string is in connection.
			// int responseCode = connection.getResponseCode();
			connection.getResponseCode();
			//System.out.println("Send GET to URL:"+URL+"?"+query);
			//System.out.println("ResponseCode:"+responseCode);
			
			/*Read data*/
			StringBuilder response = new StringBuilder();
			try(BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));){
				String inputline;
				while((inputline = in.readLine())!=null) {response.append(inputline);}
			}
			/*resovle json*/
			JSONObject obj = new JSONObject(response.toString());
			if(obj.isNull(EMBEDDED)) return new ArrayList<Item>();
			JSONObject embedded = (JSONObject) obj.get(EMBEDDED);
			JSONArray events = (JSONArray) embedded.get(EVENTS);
			
			return getItemList(events);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ArrayList<Item>();
	}
	
	private void queryAPI(double lat,double lon) {
		//test function of search, print for default
		List<Item> itemList = search(lat,lon,null);
		for(Item item:itemList) {
			JSONObject obj = item.toJSONObject();
			System.out.println(obj);
		}
	}
	
	/**
	 * Main entry for sample TicketMaster API requests.
	 */
	public static void main(String[] args) {
		TicketMasterAPI tmApi = new TicketMasterAPI();
		// Mountain View, CA
		// tmApi.queryAPI(37.38, -122.08);
		// London, UK
		// tmApi.queryAPI(51.503364, -0.12);
		// Houston, TX
		tmApi.queryAPI(43.083320, -89.372477);
	}

	
}
