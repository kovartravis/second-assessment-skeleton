package com.example.assess2.objects;

import java.util.List;

import com.example.assess2.objectsdto.TweetDto;

public class Context {

	private TweetDto target;
	private List<TweetDto> before;
	private List<TweetDto> after;
	
	public Context() {
		
	}
	
	public Context(TweetDto target, List<TweetDto> before, List<TweetDto> after) {
		super();
		this.target = target;
		this.before = before;
		this.after = after;
	}

	public TweetDto getTarget() {
		return target;
	}

	public void setTarget(TweetDto target) {
		this.target = target;
	}

	public List<TweetDto> getBefore() {
		return before;
	}

	public void setBefore(List<TweetDto> before) {
		this.before = before;
	}

	public List<TweetDto> getAfter() {
		return after;
	}

	public void setAfter(List<TweetDto> after) {
		this.after = after;
	}
	
	
}
