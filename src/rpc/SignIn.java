package rpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;


import db.DBConnection;
import db.DBConnectionFactory;

/**
 * Servlet implementation class SignIn
 */
@WebServlet("/sign")
public class SignIn extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SignIn() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBConnection conn = DBConnectionFactory.getConnection();
		JSONObject input = RpcHelper.readJSONObject(request);
		try {
			String user_id = input.getString("user_id");
			String pwd = input.getString("pwd");
			String password = input.getString("password");
			String first_name = input.getString("first_name");
			String last_name = input.getString("last_name");
			JSONObject obj = new JSONObject();
			
			String str = conn.signIn(user_id, password, first_name, last_name);
			switch (str) {
			case "Success":
				obj.put("status", "OK");
				obj.put("user_id", user_id);
				break;
			case "Exist":
				obj.put("status", "EXIST");
				obj.put("message", "* Exsits username :  " + user_id +". Please login.");
				break;
			case "Null":
				obj.put("status", "NULL");
				List<String> li_null = new ArrayList<>();
				if(user_id.isEmpty()) li_null.add("* user_id cannot be null.");
				if(pwd.isEmpty()) li_null.add("* password cannot be null.");
				if(first_name.isEmpty()) li_null.add("* first_name cannot be null.");
				if(last_name.isEmpty()) li_null.add("* last_name cannot be null.");
				obj.put("message", String.join("<br />", li_null));
				break;
			default:
				obj.put("status", "FAIL");
				obj.put("message", "* Something wrong happens, Please try again.");
				break;
			}
			RpcHelper.writeJsonObject(response, obj);
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}

}
