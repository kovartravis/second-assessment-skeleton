package com.example.assess2.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.assess2.exceptions.TagDoesNotExistException;
import com.example.assess2.objects.Hashtag;
import com.example.assess2.objectsdto.TweetDto;
import com.example.assess2.repositories.HashtagRepository;

@Service
public class HashtagService {
	
	private HashtagRepository hashtagRepo;
	private TweetService tweetService;

	public HashtagService(HashtagRepository hashtagRepo, TweetService tweetService) {
		this.hashtagRepo = hashtagRepo;
		this.tweetService = tweetService;
	}

	public List<Hashtag> getAll() {
		return hashtagRepo.findAll();
	}

	public List<TweetDto> getAllByLabel(String label) throws TagDoesNotExistException{
		if(hashtagRepo.existsByLabel(label)) {
			return tweetService.getTweetsByTag(hashtagRepo.findByLabel(label));
		}else {
			throw new TagDoesNotExistException();
		}
	}
	
	public Hashtag getTagById(String label) {
		return hashtagRepo.findByLabel(label);
	}
	
	public Hashtag newHashtag(Hashtag hashtag) {
		return hashtagRepo.save(hashtag);
	}

}
