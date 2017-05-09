package haitao;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class SubmitServlet
 */
public class SubmitServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String filePath = null;
	private static Object lockObject = new Object();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SubmitServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String name = request.getParameter("name")==null?"":request.getParameter("name");
		String id = request.getParameter("id")==null?"":request.getParameter("id");
		String type = request.getParameter("type")==null?"":request.getParameter("type");
		if(filePath == null){
			filePath = this.getInitParameter("filePath");
		}
		JSONObject result = new JSONObject();
		JSONObject status = new JSONObject();
		
		JSONObject body = new JSONObject();
		JSONArray orgArray = new JSONArray();
		JSONObject haitaoOrg = new JSONObject();
		
		
		if(type.equalsIgnoreCase("list")){
			InputStream in = null;
			try{
				Properties prop = new Properties();     
				in = new BufferedInputStream (new FileInputStream(filePath));
				prop.load(in);
				Iterator<String> it=prop.stringPropertyNames().iterator();
				JSONArray appArray = new JSONArray();
				while(it.hasNext()){
					JSONObject app = new JSONObject();
					String key=it.next();
					app.put("userName", key);
					app.put("userId", prop.getProperty(key));
					appArray.add(app);
				}
				haitaoOrg.put("name", "HAITAO");
				haitaoOrg.put("apps", appArray);
				orgArray.add(haitaoOrg);
				body.put("orgs", orgArray);
				result.put("body", body);
				status.put("code", "0000");
			}catch(Exception e){
				status.put("code", "9999");
				status.put("message", e.getMessage());
			}finally{
				if (in!=null){
					in.close();
				}
			}
		}else{
			synchronized(lockObject){
				FileWriter fwriter = null;
				try{
					fwriter = new FileWriter(new File(filePath), true);
					fwriter.append(name+ " = " + id +"\r\n");
					fwriter.flush();
					status.put("code", "0000");
				}catch(Exception e){
					status.put("code", "9999");
					status.put("message", e.getMessage());
				}finally{
					if(fwriter != null){
						fwriter.flush();
						fwriter.close();
					}
				}
			}
		}
		result.put("status", status);
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.getWriter().write(result.toString());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

}
