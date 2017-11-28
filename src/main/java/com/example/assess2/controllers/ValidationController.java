package com.example.assess2.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.assess2.objects.Credentials;
import com.example.assess2.services.ValidationService;

@CrossOrigin
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
	
	@GetMapping("/username/exists/@{username}")
	public Boolean getUsernameExists(@PathVariable String username) {
		return validationService.userExistsAndActiveIgnoreCase(username);
	}
	
	@GetMapping("/username/available/@{username}")
	public Boolean getUsernameAvaliable(@PathVariable String username) {
		return !validationService.userExistsIgnoreCase(username);
	}
	
	@PostMapping("/username/credentials")
	public Boolean getCredentialsCorrect(@RequestBody Credentials creds) {
			return validationService.checkCredentials(creds.getUsername(), creds);
	}

}
