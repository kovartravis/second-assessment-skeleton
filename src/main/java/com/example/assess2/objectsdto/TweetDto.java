package com.example.assess2.objectsdto;

import java.sql.Timestamp;

import com.example.assess2.objects.Tweet;

public class TweetDto {
	private Integer id;
	private UserDto author;
	private Timestamp posted;
	private String content;
	private Tweet inRepostOf;
	private Tweet inReplyTo;
	
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
	public Tweet getInRepostOf() {
		return inRepostOf;
	}
	public void setInRepostOf(Tweet inRepostOf) {
		this.inRepostOf = inRepostOf;
	}
	public Tweet getInReplyTo() {
		return inReplyTo;
	}
	public void setInReplyTo(Tweet inReplyTo) {
		this.inReplyTo = inReplyTo;
	}
	
	
}
