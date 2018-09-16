package rpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import entity.Item;

public class RpcHelper {
	
	
	public static void writeJsonArray(HttpServletResponse response, JSONArray array) throws IOException{
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(array);
		out.close();
	}

    // Writes a JSONObject to http response.
	public static void writeJsonObject(HttpServletResponse response, JSONObject obj) throws IOException {
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(obj);
		out.close();
	}
	
	public static JSONObject readJSONObject(HttpServletRequest request) {
		StringBuilder str = new StringBuilder();
		try(BufferedReader reader = request.getReader()){
			String inputline;
			while((inputline = reader.readLine())!=null) {str.append(inputline);}
	   		 return new JSONObject(str.toString());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new JSONObject();
	}

	//For J-unit test demonstration.
	 public static JSONArray getJSONArray(List<Item> items) {
		    JSONArray result = new JSONArray();
		    try {
		      for (Item item : items) {
		        result.put(item.toJSONObject());
		      }
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
		    return result;
	}

}
