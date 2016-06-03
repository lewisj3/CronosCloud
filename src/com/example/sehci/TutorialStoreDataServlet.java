package com.example.sehci;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.jdo.PersistenceManager;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class TutorialStoreDataServlet extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();

		PersistenceManager pm = PMF.getPMF().getPersistenceManager();
		try {
			long id;
			try {
				id = Long.parseLong(req.getParameter("id") + "");
			} catch (NumberFormatException nfe) {
				id = -1L;
			}
			String title = req.getParameter("title");
			String description = req.getParameter("description");

			if (id < 0)
				throw new IllegalArgumentException("Invalid course id");
			if (title == null || title.length() == 0)
				throw new IllegalArgumentException("Invalid course title");
			if (description == null || description.length() == 0)
				throw new IllegalArgumentException("Invalid course description");

			Course course = new Course();
			course.setId(id);
			course.setTitle(title);
			course.setDescription(description);
			course.setLastModified(new Date());
			pm.makePersistent(course);

			out.write(formatAsJson(course));
		} catch (IllegalArgumentException iae) {
			out.write(formatAsJson(iae));
		} finally {
			pm.close();
		}
	}

	public static String formatAsJson(Exception e) {
		HashMap<String, String> obj = new HashMap<String, String>();
		obj.put("errormsg", e.getMessage());

		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		String rv = gson.toJson(obj);
		return rv;
	}

	public static String formatAsJson(Course course) {
		HashMap<String, String> obj = new HashMap<String, String>();
		obj.put("id", Long.toString(course.getId()));
		obj.put("title", course.getTitle());
		obj.put("description", course.getDescription());
		obj.put("modified", Long.toString(course.getLastModified() != null ? course.getLastModified().getTime() : 0L));

		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		String rv = gson.toJson(obj);
		return rv;
	}
}
