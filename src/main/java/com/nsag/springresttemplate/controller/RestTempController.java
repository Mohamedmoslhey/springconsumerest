package com.nsag.springresttemplate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nsag.springresttemplate.entity.JwtResponse;
import com.nsag.springresttemplate.entity.Role;
import com.nsag.springresttemplate.entity.User;

@RestController
@RequestMapping("/api/v1")
public class RestTempController {

	@Autowired
	private RestTemplate restTemplate;
	
	private static final String REGISTRATION_URL = "http://localhost:8080/register";
	private static final String AUTHENTICATION_URL = "http://localhost:8080/authenticate";
	private static final String HELLO_URL = "http://localhost:8080/api/hello";
	

	
	@GetMapping({"/getResponse"})
	public String getResponse() throws JsonProcessingException  {
		
		

		String response = null;
		// create user registration object
		HttpEntity<String> registrationEntity = userRegistration();

		try {
			// Register User
			ResponseEntity<String> registrationResponse = restTemplate.exchange(REGISTRATION_URL, HttpMethod.POST,
					registrationEntity, String.class);
			   // if the registration is successful		
			if (registrationResponse.getStatusCode().equals(HttpStatus.OK)) {

				// create user authentication object
				HttpEntity<String> authenticationEntity = UserAuthication();
				
				// Authenticate User and get JWT
				ResponseEntity<JwtResponse> authenticationResponse = restTemplate.exchange(AUTHENTICATION_URL,
						HttpMethod.POST, authenticationEntity, JwtResponse.class);
					
				// if the authentication is successful		
				if (authenticationResponse.getStatusCode().equals(HttpStatus.OK)) {
					HttpEntity<String> jwtEntity = TokenProcess(authenticationResponse);
					// Use Token to get Response
					ResponseEntity<String> helloResponse = restTemplate.exchange(HELLO_URL, HttpMethod.GET, jwtEntity,
							String.class);
					if (helloResponse.getStatusCode().equals(HttpStatus.OK)) {
						response = helloResponse.getBody();
					}
				}
			}
		} catch (Exception ex) {
			System.out.println(ex);
		}
		return response;
	}


	private HttpEntity<String> TokenProcess(ResponseEntity<JwtResponse> authenticationResponse) {
		String token =  authenticationResponse.getBody().getToken();
		HttpHeaders headers = getHeaders();
		headers.set("Authorization", token);
		HttpEntity<String> jwtEntity = new HttpEntity<String>(headers);
		return jwtEntity;
	}


	private HttpEntity<String> UserAuthication() throws JsonProcessingException {
		User authenticationUser = getAuthenticationUser();

		// convert the user authentication object to JSON
		String authenticationBody = getBody(authenticationUser);
		
		// create headers specifying that it is JSON request
		HttpHeaders authenticationHeaders = getHeaders();
		
		HttpEntity<String> authenticationEntity = new HttpEntity<String>(authenticationBody,
				authenticationHeaders);
		return authenticationEntity;
	}


	private HttpEntity<String> userRegistration() throws JsonProcessingException {
		User registrationUser = getRegistrationUser();
		// convert the user registration object to JSON
		String registrationBody = getBody(registrationUser);
		// create headers specifying that it is JSON request
		HttpHeaders registrationHeaders = getHeaders();
		HttpEntity<String> registrationEntity = new HttpEntity<String>(registrationBody, registrationHeaders);
		return registrationEntity;
	}
	

	private User getAuthenticationUser() {
		User user = new User();
		user.setUsername("Ahmed");
		user.setPassword("Ahmed");
		return user;
	}


	private HttpHeaders getHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		return headers;
	}


	private String getBody(User registrationUser) throws JsonProcessingException {
		// TODO Auto-generated method stub
		return  new ObjectMapper().writeValueAsString(registrationUser);
	}


	private User getRegistrationUser() {
		Role reRole = new Role();
		reRole.setRole("ROLE_ADMIN");
		System.out.println("reRole"+reRole.getRole());
		User registerUser = new User();
		registerUser.setUsername("Ahmed");
		registerUser.setPassword("Ahmed");
		registerUser.setRole(reRole);
		return registerUser;
	}


	
}
