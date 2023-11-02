package com.webapp.genericiamendpoints.genericendpoints.model;

import java.util.Collections;
import java.util.List;

import lombok.Data;

@Data
public class RoleOperation {
	private String operation;
	private String id;
	private List<String> roles = Collections.emptyList();;
}
