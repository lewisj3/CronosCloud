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
		this.name = new String(name != null ? name : "TheTaskBroke");
	}

	public void setDone(Boolean done) {
		this.done = (done != null ? done : true);
	}
	
	public void setDate(Date date) {
		this.date = (date != null ? date : new Date(116, 6, 4));
	}

}
