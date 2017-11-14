package com.example.assess2.controllers;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.assess2.exceptions.TagDoesNotExistException;
import com.example.assess2.exceptions.UserDoesNotExistException;
import com.example.assess2.objectsdto.TweetDto;
import com.example.assess2.objectsdto.TweetGrabData;
import com.example.assess2.services.TweetService;

@RestController
@RequestMapping("/tweets")
public class TweetController {
	
	private TweetService tweetService;

	public TweetController(TweetService tweetService) {
		this.tweetService = tweetService;
	}
	
	@GetMapping
	public List<TweetDto> getAll(){
		return tweetService.getAll();
	}
	
	@GetMapping("/{id}")
	public TweetDto getTweetById(@PathVariable Integer id, HttpServletResponse response) throws IOException {
		try {
			return tweetService.getTweetById(id);
		} catch (TagDoesNotExistException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
	}
	
	@PostMapping
	public TweetDto postTweet(@RequestBody TweetGrabData tweetGrabData, HttpServletResponse response) throws IOException {
		try {
			return tweetService.postTweet(tweetGrabData.content, tweetGrabData.credentials);
		} catch (UserDoesNotExistException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
	}

}
