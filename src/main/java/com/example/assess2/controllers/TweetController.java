package com.example.assess2.controllers;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.assess2.exceptions.CredentialsDoNotMatchException;
import com.example.assess2.exceptions.TagDoesNotExistException;
import com.example.assess2.exceptions.TweetDoesNotExistException;
import com.example.assess2.exceptions.UserDoesNotExistException;
import com.example.assess2.objects.Context;
import com.example.assess2.objects.Credentials;
import com.example.assess2.objects.Hashtag;
import com.example.assess2.objectsdto.CredentialsGrabData;
import com.example.assess2.objectsdto.TweetDto;
import com.example.assess2.objectsdto.TweetGrabData;
import com.example.assess2.objectsdto.UserDto;
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
	
	@GetMapping("/{id}/tags")
	public List<Hashtag> getTweetTags(@PathVariable Integer id, HttpServletResponse response) throws IOException{
		try {
			return tweetService.getTweetTags(id);
		} catch (TweetDoesNotExistException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
	}
	
	@GetMapping("/{id}/context")
	public Context getTweetContext(@PathVariable Integer id, HttpServletResponse response) throws IOException{
		try {
			return tweetService.getTweetContext(id);
		} catch (TweetDoesNotExistException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
	}
	
	@GetMapping("/{id}/reposts")
	public List<TweetDto> getReposts(@PathVariable Integer id, HttpServletResponse response) throws IOException{
		try {
			return tweetService.getReposts(id);
		} catch (TweetDoesNotExistException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
	}
	
	@GetMapping("/{id}/mentions")
	public List<UserDto> getMentions(@PathVariable Integer id, HttpServletResponse response) throws IOException{
		try {
			return tweetService.getMentions(id);
		} catch (TweetDoesNotExistException e) {
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
		} catch (CredentialsDoNotMatchException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		}
	}
	
	@PostMapping("/{id}/like")
	public void likeTweet(@PathVariable Integer id, @RequestBody Credentials credentials, HttpServletResponse response) throws IOException {
		try {
			tweetService.likeTweet(id, credentials);
		} catch (TweetDoesNotExistException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		} catch (CredentialsDoNotMatchException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		} catch (UserDoesNotExistException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}
	
	@PostMapping("/{id}/reply")
	public TweetDto replyTo(@PathVariable Integer id, @RequestBody TweetGrabData tweetGrabData, HttpServletResponse response) throws IOException {
		try {
			return tweetService.replyTo(id, tweetGrabData.credentials, tweetGrabData.content);
		} catch (TweetDoesNotExistException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		} catch (CredentialsDoNotMatchException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		} catch (UserDoesNotExistException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
	}
	
	@PostMapping("/{id}/repost")
	public TweetDto repost(@PathVariable Integer id, @RequestBody CredentialsGrabData credentials, HttpServletResponse response) throws IOException {
		try {
			Credentials creds = credentials.credentials;
			System.out.println(creds.getUsername());
			System.out.println(creds.getPassword());
			return tweetService.repost(id, creds);
		} catch (TweetDoesNotExistException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		} catch (CredentialsDoNotMatchException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		} catch (UserDoesNotExistException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
	}
	
	@DeleteMapping("/{id}")
	public TweetDto deleteTweet(@PathVariable Integer id, HttpServletResponse response) throws IOException {
		try {
			return tweetService.deleteTweet(id);
		} catch (TweetDoesNotExistException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
	}
	
	

}
