package com.example.assess2.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.assess2.services.ValidationService;

@RestController
@RequestMapping("/validate")
public class ValidationController {
	
	private ValidationService validationService;

	public ValidationController(ValidationService validationService) {
		this.validationService = validationService;
	}
	
	@GetMapping("/tag/exists/{label}")
	public Boolean getLabelExists(@PathVariable String label) {
		return validationService.tagExists(label);
	}
	
	@GetMapping("/validate/username/exists/@{username}")
	public Boolean getUsernameExists(@PathVariable String username) {
		return validationService.userExists(username);
	}
	
	@GetMapping("/validate/username/avaliable/@{username}")
	public Boolean getUsernameAvaliable(@PathVariable String username) {
		return !validationService.userExistsAndActive(username);
	}

}
