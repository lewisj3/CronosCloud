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
import javax.jdo.Transaction;
import javax.servlet.http.*;


@SuppressWarnings("serial")
public class CronosServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		PersistenceManager pm = PMF.getPMF().getPersistenceManager();
		String op = req.getParameter("op");
		if("cron".equals(op)){
			Transaction trans = pm.currentTransaction();
			trans.begin();
			Task statsTask = (Task) pm.getObjectById(Task.class, "Stats1");
			Query query = pm.newQuery(Task.class, "id > 1");
			List<Task> tasks = (List<Task>) query.execute();
			int totalAttempted = 0, totalCompleted = 0;
			for(Task tempTask : tasks){
				pm.refresh(tempTask);
				totalAttempted += tempTask.getAttempted();
				totalCompleted += tempTask.getCompleted();
			}
			statsTask.setAttempted(totalAttempted);
			statsTask.setCompleted(totalCompleted);
			pm.makePersistent(statsTask);
			trans.commit();
			HashMap<String, Integer> obj = new HashMap<String, Integer>();
			obj.put("Success", new Integer(1));
			out.write(new Gson().toJson(obj));
			
		}else if("userStats".equals(op)){
			String requestQuery = req.getParameter("query");
			Query query = pm.newQuery(requestQuery);
			List<Task> tl = (List<Task>) query.execute();
		} else if ("taskStats".equals(op)) {
			int id = Integer.parseInt(req.getParameter("id"));
			String name = req.getParameter("name");
			Task tempTask = (Task) pm.getObjectById(Task.class, name + id);
			pm.refresh(tempTask);
			HashMap<String, Integer> obj = new HashMap<String, Integer>();
			obj.put("Attempted", Integer.valueOf(tempTask.getAttempted()));
			obj.put("Completed", Integer.valueOf(tempTask.getCompleted()));
			out.write(new Gson().toJson(obj));
		} else {
			Query query = pm.newQuery(Task.class, "id == 1");
			List<Task> task = (List<Task>) query.execute();
			pm.refresh(task.get(0));
			int attempt = task.get(0).getAttempted();
			int complete = task.get(0).getCompleted();
			HashMap<String, Integer> obj = new HashMap<String, Integer>();
			obj.put("Attempted", new Integer(attempt));
			obj.put("Completed", new Integer(complete));
			out.write(new Gson().toJson(obj));
		}
		pm.close();
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		PersistenceManager pm = PMF.getPMF().getPersistenceManager();
		String op = req.getParameter("op");
			try {
				switch (op) {
				case "new":
					handleNewTask(req, out, pm);
					break;
				case "update":
					handleTaskUpdates(req, out, pm);
					break;
				case "taskDel":
					handleTaskDel(req, out, pm);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				out.write(e.getMessage());
			} finally {
				pm.close();
			}
	}
	
	private void handleTaskDel(HttpServletRequest req, PrintWriter out, PersistenceManager pm)
	{
		Transaction trans = pm.currentTransaction();
		trans.begin();
		int id = Integer.parseInt(req.getParameter("id"));
		String name = req.getParameter("name");
		Task tempTask = (Task) pm.getObjectById(Task.class, name + id);
		pm.deletePersistent(tempTask);
		trans.commit();
		HashMap<String, Integer> obj = new HashMap<String, Integer>();
		obj.put("Success", new Integer(1));
		out.write(new Gson().toJson(obj));
	}
	

	private void handleTaskUpdates(HttpServletRequest req, PrintWriter out, PersistenceManager pm) throws IOException {
		
		if (req.getParameter("multiple").equals("false")){
			Gson gson = new Gson();
			BufferedReader reader = req.getReader();
			Task task = (Task) gson.fromJson(reader, Task.class);
			Task temp = (Task) pm.getObjectById(Task.class, task.getName() + task.getID());
			temp.setAttempted(task.getAttempted());
			temp.setCompleted(task.getCompleted());
		}else{
		Collection<Task> tasklist = readTaskList(req);
		Iterator<Task> iter = tasklist.iterator();
		while(iter.hasNext()){
			Task t = (Task) iter.next();
			Task temp = (Task) pm.getObjectById(Task.class, t.getName() + t.getID());
			temp.setAttempted(t.getAttempted());
			temp.setCompleted(t.getCompleted());
		}
		}
		HashMap<String, Integer> obj = new HashMap<String, Integer>();
		obj.put("Success", new Integer(1));
		out.write(new Gson().toJson(obj));
	}
	
	private Collection<Task> readTaskList(HttpServletRequest req) throws IOException{
		Gson mGson = new Gson();
		BufferedReader reader = req.getReader();
		Type collectionType = new TypeToken<Collection<Task>>(){}.getType();
		Collection<Task> enums = mGson.fromJson(reader, collectionType);
		return enums;
	}

	private void handleNewTask(HttpServletRequest req, PrintWriter out, PersistenceManager pm) {
		String name = req.getParameter("name");
		int id = Integer.parseInt(req.getParameter("id"));
		int attempted = 0, completed = 0;
		Task t = new Task();
		t.setID(id);
		t.setName(name);
		t.setAttempted(attempted);
		t.setCompleted(completed);
		t.setKey();
		pm.makePersistent(t);
		HashMap<String, Integer> obj = new HashMap<String, Integer>();
		obj.put("Success", new Integer(1));
		out.write(new Gson().toJson(obj));
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
		obj.put("id", String.valueOf(task.getID()));
		obj.put("name", task.getName());
		obj.put("completed", Integer.toString(task.getCompleted()));
		obj.put("attempted", Integer.toString(task.getAttempted()));
		
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		String rv = gson.toJson(obj);
		return rv;
	}
	
	


}
