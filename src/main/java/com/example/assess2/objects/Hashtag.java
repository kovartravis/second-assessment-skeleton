package com.example.assess2.objects;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Hashtag {

	@Id
	@GeneratedValue
	private Integer id;
	
	private String label;
	private Timestamp firstUsed;
	private Timestamp lastUsed;
	
	public Hashtag() {
		
	}
	
	public Hashtag(Integer id, String label, Timestamp firstUsed, Timestamp lastUsed) {
		super();
		this.id = id;
		this.label = label;
		this.firstUsed = firstUsed;
		this.lastUsed = lastUsed;
	}

	
	public Hashtag(String s, long firstUsed) {
		this.id = null;
		this.label = s;
		this.firstUsed = new Timestamp(firstUsed);
		this.lastUsed = new Timestamp(firstUsed);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Timestamp getFirstUsed() {
		return firstUsed;
	}

	public void setFirstUsed(Timestamp firstUsed) {
		this.firstUsed = firstUsed;
	}

	public Timestamp getLastUsed() {
		return lastUsed;
	}

	public void setLastUsed(Timestamp lastUsed) {
		this.lastUsed = lastUsed;
	}
	
	public void setLastUsed(Long lastUsed) {
		this.lastUsed = new Timestamp(lastUsed);
	}
	
	
}
