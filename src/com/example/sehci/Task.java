package com.example.sehci;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Task {
	@Persistent
	private String name;

	@Persistent
	private int completed;
	
	@Persistent
	private int attempted;
	
	@PrimaryKey
	private String key;
	
	@Persistent
	private int id;
	

	public String getName(){
		return (name != null ? name : "No Name Supplied To Task");
	}
	
	

	public int getCompleted(){
		return completed;
	}
	
	public int getAttempted(){
		return attempted;
	}
	
	public int getID(){
		return id;
	}
	
	public void setID(int id){
		this.id = id;
	}
	
	
	public void setCompleted(int completed){
		this.completed = completed;
	}
	
	public void setAttempted(int attempted){
		this.attempted = attempted;
	}
	
	
	public void setKey(){
		key = name + id;
	}

	public void setName(String name) {
		this.name = name;
	}

}
