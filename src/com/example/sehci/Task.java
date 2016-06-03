package com.example.sehci;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Task {
	@PrimaryKey
	private String name;

	@Persistent
	private Boolean done;

	@Persistent
	private Date date;
	

	public String getName(){
		return name;
	}
	
	public Boolean getDone(){
		return done;
	}
	
	public Date getDate() {
		return date;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDone(Boolean done) {
		this.done = done;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}

}
