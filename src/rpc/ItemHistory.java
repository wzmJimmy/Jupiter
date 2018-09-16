package rpc;

import java.io.IOException;
import java.util.ArrayList;
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

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

/**
 * Servlet implementation class ItemHistory
 */
@WebServlet("/history")
public class ItemHistory extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ItemHistory() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// allow access only if session exists
//		HttpSession session = request.getSession(false);
//		if (session == null) {
//			response.setStatus(403);
//			return;
//		}
//		// optional
//		String user_id = session.getAttribute("user_id").toString();
		String user_id = request.getParameter("user_id");
		
		DBConnection conn = DBConnectionFactory.getConnection();
		Set<Item> items = conn.getFavoriteItems(user_id);
		JSONArray array = new JSONArray();
		for (Item item : items) {
			JSONObject object = item.toJSONObject();
			try {
				object.put("favorite", "true");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			array.put(object);
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
		// allow access only if session exists
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(403);
			return;
		}
		// optional
		String user_id = session.getAttribute("user_id").toString();

		DBConnection conn = DBConnectionFactory.getConnection();
		JSONObject input = RpcHelper.readJSONObject(request);
		try {
			// String user_id = input.getString("user_id");
			JSONArray favorite = input.getJSONArray("favorite");
			List<String> itemids = new ArrayList<>();
			for (int i = 0; i < favorite.length(); ++i) {
				itemids.add(favorite.getString(i));
			}
			conn.setFavoriteItems(user_id, itemids);
			RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// allow access only if session exists
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(403);
			return;
		}
		// optional
		String user_id = session.getAttribute("user_id").toString();

		DBConnection conn = DBConnectionFactory.getConnection();
		JSONObject input = RpcHelper.readJSONObject(request);
		try {
			// String user_id = input.getString("user_id");
			JSONArray favorite = input.getJSONArray("favorite");
			List<String> itemids = new ArrayList<>();
			for (int i = 0; i < favorite.length(); ++i) {
				itemids.add(favorite.getString(i));
			}
			conn.unsetFavoriteItems(user_id, itemids);
			RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}

}
