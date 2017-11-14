package com.example.assess2.controllers;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.assess2.exceptions.TagDoesNotExistException;
import com.example.assess2.objects.Hashtag;
import com.example.assess2.objectsdto.TweetDto;
import com.example.assess2.services.HashtagService;

@RestController
@RequestMapping("/tags")
public class HashtagController {
	
	private HashtagService hashtagService;

	public HashtagController(HashtagService hashtagService) {
		this.hashtagService = hashtagService;
	}
	
	@GetMapping
	public List<Hashtag> getAll(){
		return hashtagService.getAll();
	}
	
	@GetMapping("/{label}")
	public List<TweetDto> getAllByLabel(@PathVariable String label, HttpServletResponse response) throws IOException {
		try {
			return hashtagService.getAllByLabel(label);
		} catch (TagDoesNotExistException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
	}

}
