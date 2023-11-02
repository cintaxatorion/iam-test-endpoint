package com.webapp.genericiamendpoints.genericendpoints.model;

import java.util.List;

import lombok.Data;

@Data
public class Identity {
	
	private String id;
	private String firstName;
	private String lastName;
	private List<String> roles;
	
}
