package com.example.assess2.objectsdto;

import java.sql.Timestamp;

import javax.persistence.Embeddable;

import com.example.assess2.objects.Profile;

@Embeddable
public class UserDto {

	private String username;
	private Timestamp joined;
	private Profile profile;
	
	public UserDto() {
		
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Timestamp getJoined() {
		return joined;
	}
	public void setJoined(Timestamp joined) {
		this.joined = joined;
	}
	public Profile getProfile() {
		return profile;
	}
	public void setProfile(Profile profile) {
		this.profile = profile;
	}
	
	
}
