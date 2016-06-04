package com.example.sehci;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Task {
	@Persistent
	private String name;

	@Persistent
	private Boolean done;

	@Persistent
	private Date date;
	
	@PrimaryKey
	private String key;
	

	public String getName(){
		return (name != null ? name : "No Name Supplied To Task");
	}
	
	public Boolean getDone(){
		return (done != null ? done : false);
	}
	
	public Date getDate() {
		return (date != null ? date : new Date(116, 6, 3));
	}
	
	public void setKey(){
		key = name + date.toString();
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
