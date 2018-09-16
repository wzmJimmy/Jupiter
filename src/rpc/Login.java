package rpc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;

/**
 * Servlet implementation class Login
 */
@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Login() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		DBConnection conn = DBConnectionFactory.getConnection();
		JSONObject obj = new JSONObject();
		
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(403);
			try {
				obj.put("status", "Session invalid!");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			String userid = (String) session.getAttribute("user_id");
			String name = conn.getFullname(userid);
			try {
				obj.put("status", "OK");
				obj.put("user_id", userid);
				obj.put("name", name);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		RpcHelper.writeJsonObject(response, obj);
		conn.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		DBConnection conn = DBConnectionFactory.getConnection();
		JSONObject input = RpcHelper.readJSONObject(request);
		try {
			String user_id = input.getString("user_id");
			String password = input.getString("password");
			JSONObject obj = new JSONObject();
			if (conn.verifyLogin(user_id, password)) {
				HttpSession session = request.getSession();
				session.setAttribute("user_id", user_id);
				session.setMaxInactiveInterval(20 * 60); // unit = second;
				String name = conn.getFullname(user_id);
				obj.put("status", "OK");
				obj.put("user_id", user_id);
				obj.put("name", name);

			} else {
				response.setStatus(401);
			}
			RpcHelper.writeJsonObject(response, obj);

		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}

}
