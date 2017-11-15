package com.example.assess2.objectsdto;

import java.sql.Timestamp;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class TweetDto {
	@Id
	private Integer id;
	@Embedded
	private UserDto author;
	
	private Timestamp posted;
	private String content;
	
	@ManyToOne
	private TweetDto inRepostOf;
	
	@ManyToOne
	private TweetDto inReplyTo;
	
	public TweetDto() {
		
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public UserDto getAuthor() {
		return author;
	}
	public void setAuthor(UserDto author) {
		this.author = author;
	}
	public Timestamp getPosted() {
		return posted;
	}
	public void setPosted(Timestamp posted) {
		this.posted = posted;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public TweetDto getInRepostOf() {
		return inRepostOf;
	}
	public void setInRepostOf(TweetDto inRepostOf) {
		this.inRepostOf = inRepostOf;
	}
	public TweetDto getInReplyTo() {
		return inReplyTo;
	}
	public void setInReplyTo(TweetDto inReplyTo) {
		this.inReplyTo = inReplyTo;
	}
	
	
}
