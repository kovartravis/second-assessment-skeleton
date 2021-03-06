package com.example.assess2.services;

import org.springframework.stereotype.Service;

import com.example.assess2.objects.Credentials;
import com.example.assess2.objects.User;
import com.example.assess2.repositories.HashtagRepository;
import com.example.assess2.repositories.TweetRepository;
import com.example.assess2.repositories.UserRepository;

@Service
public class ValidationService {

	private UserRepository userRepo;
	private HashtagRepository hashtagRepo;
	private TweetRepository tweetRepo;

	public ValidationService(UserRepository userRepo, HashtagRepository hashtagRepo, TweetRepository tweetRepo) {
		this.userRepo = userRepo;
		this.hashtagRepo = hashtagRepo;
		this.tweetRepo = tweetRepo;
	}

	public boolean userExists(String username) {
		return userRepo.existsByCredentialsUsername(username);
	}

	public boolean userExistsAndActive(String username) {
		return userRepo.existsByCredentialsUsernameAndActiveIsTrue(username);
	}
	
	public boolean userExistsIgnoreCase(String username) {
		return userRepo.existsByCredentialsUsernameIgnoreCase(username);
	}
	
	public boolean userExistsAndActiveIgnoreCase(String username) {
		return userRepo.existsByCredentialsUsernameIgnoreCaseAndActiveIsTrue(username);
	}
	public boolean tagExists(String label) {
		return hashtagRepo.existsByLabel(label);
	}
	
	public boolean tweetExistsById(Integer id) {
		return tweetRepo.existsByIdAndDeletedIsFalse(id);
	}
	
	public boolean checkCredentials(String username, Credentials creds) {
		if(username == null || creds == null) {
			return false;
		}
		if(userRepo.findByCredentialsUsername(username) == null) {
			return false;
		}
		User user = userRepo.findByCredentialsUsername(username);
		if(user.getCredentials().getPassword().equals(creds.getPassword())) return true;
		else return false;
	}
}
