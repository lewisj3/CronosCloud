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
	private String username;
	

	public String getName(){
		return (name != null ? name : "No Name Supplied To Task");
	}
	
	

	public int getCompleted(){
		return completed;
	}
	
	public int getAttempted(){
		return attempted;
	}
	
	public String getUsername(){
		return username;
	}
	
	public void setCompleted(int completed){
		this.completed = completed;
	}
	
	public void setAttempted(int attempted){
		this.attempted = attempted;
	}
	
	public void setUsername(String username){
		this.username = username;
	}
	
	public void setKey(){
		key = name + username;
	}

	public void setName(String name) {
		this.name = name;
	}

}
