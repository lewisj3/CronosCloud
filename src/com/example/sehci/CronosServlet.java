package com.example.sehci;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.*;


@SuppressWarnings("serial")
public class CronosServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		PersistenceManager pm = PMF.getPMF().getPersistenceManager();
		String op = req.getParameter("op");
		if("cron".equals(op)){
			
		}else if("userStats".equals(op)){
			User user = User.authenticate(req, pm);
			String requestQuery = req.getParameter("query");
			Query query = pm.newQuery(requestQuery);
			List<Task> tl = (List<Task>) query.execute();
		}else{
			Query query = pm.newQuery("SELECT * FROM Task WHERE username = 'StatisticsAdmin'");
			Task task = (Task) query.execute();
			int attempt = task.getAttempted();
			int complete = task.getCompleted();
			HashMap<String, Integer> obj = new HashMap<String, Integer>();
			obj.put("Attempted", new Integer(attempt));
			obj.put("Completed", new Integer(complete));
			out.write(new Gson().toJson(obj));
		}
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		PersistenceManager pm = PMF.getPMF().getPersistenceManager();
		String op = req.getParameter("op");
			if (op == null)
				op = "list";

			try {
				switch (op) {
				case "new":
					handleNewTask(req, out, pm);
					break;
				case "update":
					handleTaskUpdates(req, out, pm);
					break;
				case "register":
					handleUserRegister(req, out, pm);
					break;
				case "login":
					handleUserLogin(req, out, pm);
					break;
				case "touch":
					handleUserSessionTouch(req, out, pm);
					break;
				case "create":
					handleCreateEntry(req, out, pm);
					break;
				case "read":
					handleReadEntry(req, out, pm);
					break;
				case "list":
					handleListEntries(req, out, pm);
					break;
				case "search":
					handleSearchEntries(req, out, pm);
					break;
				case "delete":
					handleDeleteEntry(req, out, pm);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				out.write(Util.toJsonPair("errormsg", e.getMessage()));
			} finally {
				pm.close();
			}
	}
	

	private void handleTaskUpdates(HttpServletRequest req, PrintWriter out, PersistenceManager pm) throws IOException {
		User user = User.authenticate(req, pm);
		String username = user.getUsername();
		Collection<Task> tasklist = readTaskList(req);
		Iterator<Task> iter = tasklist.iterator();
		while(iter.hasNext()){
			Task t = (Task) iter.next();
			Task temp = (Task) pm.getObjectById(Task.class, t.getName() + username);
			temp.setAttempted(t.getAttempted());
			temp.setCompleted(t.getCompleted());
		}
		out.write(Util.toJson(user));
	}
	
	private Collection<Task> readTaskList(HttpServletRequest req) throws IOException{
		Gson mGson = new Gson();
		BufferedReader reader = req.getReader();
		Type collectionType = new TypeToken<Collection<Task>>(){}.getType();
		Collection<Task> enums = mGson.fromJson(reader, collectionType);
		return enums;
	}

	private void handleNewTask(HttpServletRequest req, PrintWriter out, PersistenceManager pm) {
		User user = User.authenticate(req, pm);
		String name = req.getParameter("name");
		int attempted = 0, completed = 0;
		Task t = new Task();
		t.setUsername(user.getUsername());
		t.setName(name);
		t.setAttempted(attempted);
		t.setCompleted(completed);
		t.setKey();
		pm.makePersistent(t);
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
		obj.put("completed", Integer.toString(task.getCompleted()));
		obj.put("attempted", Integer.toString(task.getAttempted()));
		
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		String rv = gson.toJson(obj);
		return rv;
	}
	
	
	private Limerick getEntryAndVerifyOwnership(HttpServletRequest req, PersistenceManager pm) {
		User user = User.authenticate(req, pm);

		long id = Util.getLong(req, "id");
		if (id == 0)
			throw new IllegalArgumentException("Invalid or missing entry id.");

		// verify ownership
		Limerick entry = Limerick.loadById(id, pm);
		if (!user.getUsername().equals(entry.getOwner()))
			throw new IllegalStateException("Unauthorized access");
		return entry;
	}

	private void handleCreateEntry(HttpServletRequest req, PrintWriter out, PersistenceManager pm) {
		User user = User.authenticate(req, pm);

		Limerick entry = new Limerick();
		entry.setTitle(req.getParameter("title"));
		entry.setWhen(Util.getLong(req, "when"));
		entry.setBlather(req.getParameter("blather"));
		entry.setTags(req.getParameter("tags"));
		entry.setOwner(user.getUsername());

		pm.makePersistent(entry);
		out.write(Util.toJson(entry, true));
	}

	private void handleDeleteEntry(HttpServletRequest req, PrintWriter out, PersistenceManager pm) {
		Limerick entry = getEntryAndVerifyOwnership(req, pm);
		Limerick.delete(entry, pm);
	}

	private void handleListEntries(HttpServletRequest req, PrintWriter out, PersistenceManager pm) {
		User user = User.authenticate(req, pm);
		List<Limerick> entries = Limerick.listByOwner(user.getUsername(), pm);
		out.write(Util.toJson(entries));
	}

	private void handleReadEntry(HttpServletRequest req, PrintWriter out, PersistenceManager pm) {
		Limerick entry = getEntryAndVerifyOwnership(req, pm);
		out.write(Util.toJson(entry, true));
	}

	private void handleSearchEntries(HttpServletRequest req, PrintWriter out, PersistenceManager pm) {
		User user = User.authenticate(req, pm);
		List<Limerick> entries = Limerick.listByQuery(user.getUsername(), req.getParameter("tag"), pm);
		out.write(Util.toJson(entries));
	}

	/*private void handleUpdateEntry(HttpServletRequest req, PrintWriter out, PersistenceManager pm) {
		Limerick entry = getEntryAndVerifyOwnership(req, pm);
		entry.setTitle(req.getParameter("title"));
		entry.setWhen(Util.getLong(req, "when"));
		entry.setBlather(req.getParameter("blather"));
		entry.setTags(req.getParameter("tags"));
		pm.makePersistent(entry);
		out.write(Util.toJson(entry, true));
	}*/

	private void handleUserLogin(HttpServletRequest req, PrintWriter out, PersistenceManager pm) {
		User user = User.authenticate(req, pm);
		out.write(Util.toJson(user));
	}

	private void handleUserRegister(HttpServletRequest req, PrintWriter out, PersistenceManager pm) {
		User user = User.create(req.getParameter("username"), req.getParameter("password"), pm);
		out.write(Util.toJson(user));
	}

	private void handleUserSessionTouch(HttpServletRequest req, PrintWriter out, PersistenceManager pm) {
		User user = User.authenticate(req, pm);
		out.write(Util.toJson(user));
	}
}
