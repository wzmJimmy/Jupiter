package rpc;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
import db.DBConnection;
import db.DBConnectionFactory;

/**
 * Servlet implementation class SearchItem
 */
@WebServlet("/search") // endpoint
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SearchItem() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// allow access only if session exists
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(403);
			return;
		}
		// optional
		String userid = session.getAttribute("user_id").toString();
		//String userid = request.getParameter("user_id");

		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		String keyword = request.getParameter("term");
		
		DBConnection conn = DBConnectionFactory.getConnection();
		List<Item> items = conn.searchItems(lat, lon, keyword);
		Set<String> ids = conn.getFavoriteItemIds(userid);

		JSONArray array = new JSONArray();
		for (Item item : items) {
			JSONObject obj = item.toJSONObject();
			try {
				obj.put("favorite", ids.contains(item.getItemId()));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			array.put(obj);
		}
		RpcHelper.writeJsonArray(response, array);
		conn.close();

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
