package com.example.sehci;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;

@PersistenceCapable
public class Course {
	@PrimaryKey
	private Long id;

	@Persistent
	private String title;

	@Persistent
	private Text description;

	@Persistent
	private Date modified;

	public Date getLastModified() {
		return modified;
	}

	public String getDescription() {
		return description != null ? description.getValue() : "";
	}

	public long getId() {
		return id != null ? id.longValue() : -1L;
	}

	public String getTitle() {
		return title != null ? title : "";
	}

	public void setLastModified(Date modified) {
		this.modified = modified != null ? modified : new Date();
	}

	public void setDescription(String description) {
		this.description = new Text(description != null ? description : "");
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title != null ? title : "";
	}

}
