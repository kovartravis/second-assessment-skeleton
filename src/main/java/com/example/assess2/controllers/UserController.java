package com.example.assess2.controllers;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.assess2.exceptions.AlreadyFollowingUserException;
import com.example.assess2.exceptions.CredentialsDoNotMatchException;
import com.example.assess2.exceptions.NotFollowingUserException;
import com.example.assess2.exceptions.SomethingIsNullAndItShouldntBeException;
import com.example.assess2.exceptions.UserAlreadyExistsException;
import com.example.assess2.exceptions.UserDoesNotExistException;
import com.example.assess2.objects.Credentials;
import com.example.assess2.objectsdto.CredentialsGrabData;
import com.example.assess2.objectsdto.TweetDto;
import com.example.assess2.objectsdto.UserDto;
import com.example.assess2.objectsdto.UserGrabData;
import com.example.assess2.services.TweetService;
import com.example.assess2.services.UserService;

@CrossOrigin	
@RestController
@RequestMapping("users")
public class UserController {
	
	private UserService userService;
	private TweetService tweetService;

	public UserController(UserService userService, TweetService tweetService) {
		this.userService = userService;
		this.tweetService = tweetService;
	}
	
	@GetMapping
	public List<UserDto> getAllUsers(){
		return userService.getAll();
	}
	
	@GetMapping("/@{username}")
	public UserDto getUserByUsername(@PathVariable String username, HttpServletResponse response) throws IOException {
		try {
			return userService.getUserDtoByUsername(username);
		} catch (UserDoesNotExistException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
	}
	
	@GetMapping("/@{username}/feed")
	public List<TweetDto> getFeed(@PathVariable String username, HttpServletResponse response) throws IOException{
		try {
			return tweetService.getFeed(username);
		} catch (UserDoesNotExistException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
	}
	
	@GetMapping("/@{username}/tweets")
	public List<TweetDto> getTweets(@PathVariable String username, HttpServletResponse response) throws IOException{
		try {
			return tweetService.getTweets(username);
		} catch (UserDoesNotExistException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
	}
	
	@GetMapping("/@{username}/mentions")
	public List<TweetDto> getMentions(@PathVariable String username, HttpServletResponse response) throws IOException{
		try {
			return tweetService.getMentions(username);
		} catch (UserDoesNotExistException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
	}
	
	@GetMapping("/@{username}/followers")
	public List<UserDto> getFollowers(@PathVariable String username, HttpServletResponse response) throws IOException{
		try {
			return userService.getFollowers(username);
		} catch (UserDoesNotExistException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
	}
	
	@GetMapping("/@{username}/following")
	public List<UserDto> getFollowing(@PathVariable String username, HttpServletResponse response) throws IOException{
		try {
			return userService.getFollowing(username);
		} catch (UserDoesNotExistException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
	}
	
	@PostMapping
	public UserDto postUser(@RequestBody UserGrabData user, HttpServletResponse response) throws IOException {
		try {
			return userService.postUser(user.credentials, user.profile);
		} catch (UserAlreadyExistsException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return null;
		} catch (SomethingIsNullAndItShouldntBeException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return null;
		}
	}
	
	@PostMapping("/@{username}/follow")
	public void followUser(@PathVariable String username, @RequestBody Credentials credentials, HttpServletResponse response) throws IOException {
		try {
			userService.followUser(username, credentials);
		} catch (UserDoesNotExistException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		} catch (CredentialsDoNotMatchException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		} catch (AlreadyFollowingUserException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
		} catch (SomethingIsNullAndItShouldntBeException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}
	
	@PostMapping("/@{username}/unfollow")
	public void unfollowUser(@PathVariable String username, @RequestBody CredentialsGrabData credentials, HttpServletResponse response) throws IOException {
		try {
			userService.unfollowUser(username, credentials.credentials);
		} catch (UserDoesNotExistException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		} catch (CredentialsDoNotMatchException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		} catch (NotFollowingUserException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_FORBIDDEN); 
		} catch (SomethingIsNullAndItShouldntBeException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}
	
	@PatchMapping("/@{username}")
	public UserDto updateUser(@PathVariable String username, @RequestBody UserGrabData user, HttpServletResponse response) throws IOException {
		try {
			return userService.updateUser(username, user.credentials, user.profile);
		} catch (UserDoesNotExistException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		} catch (CredentialsDoNotMatchException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		} catch (SomethingIsNullAndItShouldntBeException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return null;
		}
	}
	
	@DeleteMapping("/@{username}")
	public UserDto deleteUser (@PathVariable String username, @RequestBody CredentialsGrabData credentials, HttpServletResponse response) throws IOException {
	    try {
			return userService.deleteUser(username, credentials.credentials);
		} catch (UserDoesNotExistException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		} catch (CredentialsDoNotMatchException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		}
	}

}
