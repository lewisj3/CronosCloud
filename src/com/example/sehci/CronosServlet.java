package com.example.sehci;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.jdo.PersistenceManager;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class CronosServlet extends HttpServlet {
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		
		PersistenceManager pm = PMF.getPMF().getPersistenceManager();
		
		String name = req.getParameter("name");
		Boolean done = Boolean.parseBoolean(req.getParameter("done"));
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		Date date = new Date();
		try{
			date = df.parse(req.getParameter("date"));
		} catch(Exception e){
			System.out.println(e.toString());
		}
		Task task = new Task();
		task.setName(name);
		task.setDate(date);
		task.setDone(done);
		
		pm.makePersistent(task);
		
		out.write(formatAsJson(task));
		pm.close();
	}
	

	public static String formatAsJson() {
		HashMap<String, String> obj = new HashMap<String, String>();

		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		String rv = gson.toJson(obj);
		return rv;
	}

	public static String formatAsJson(Task task) {
		HashMap<String, String> obj = new HashMap<String, String>();
		obj.put("name", task.getName());
		obj.put("done", task.getDone().toString());
		obj.put("date", task.getDate().toString());
		
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		String rv = gson.toJson(obj);
		return rv;
	}
}
