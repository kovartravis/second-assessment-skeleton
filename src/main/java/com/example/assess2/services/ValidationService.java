package com.example.assess2.services;

import org.springframework.stereotype.Service;

import com.example.assess2.repositories.HashtagRepository;
import com.example.assess2.repositories.UserRepository;

@Service
public class ValidationService {

	private UserRepository userRepo;
	private HashtagRepository hashtagRepo;

	public ValidationService(UserRepository userRepo, HashtagRepository hashtagRepo) {
		this.userRepo = userRepo;
		this.hashtagRepo = hashtagRepo;
	}

	public boolean userExists(String username) {
		return userRepo.existsByCredentialsUsername(username);
	}

	public boolean userExistsAndActive(String username) {
		return userRepo.existsByCredentialsUsernameAndActiveIsTrue(username);
	}
	
	public boolean tagExists(String label) {
		return hashtagRepo.existsByLabel(label);
	}
}
