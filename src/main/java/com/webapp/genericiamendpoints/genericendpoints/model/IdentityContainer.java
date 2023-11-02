package com.webapp.genericiamendpoints.genericendpoints.model;

import java.util.Map;

public class IdentityContainer {

	private Map<String, Identity> identities;

	public Map<String, Identity> getIdentities() {
		return identities;
	}

	public void setIdentities(Map<String, Identity> identities) {
		this.identities = identities;
	}

	@Override
	public String toString() {
		return "IdentityContainer [identities=" + identities + "]";
	}
	
}
