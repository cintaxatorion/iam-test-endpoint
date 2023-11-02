package com.webapp.genericiamendpoints.genericendpoints.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.genericiamendpoints.genericendpoints.model.Identity;
import com.webapp.genericiamendpoints.genericendpoints.model.IdentityContainer;
import com.webapp.genericiamendpoints.genericendpoints.model.RoleOperation;

@Service
public class IdentityService {

	private final ObjectMapper objectMapper;

	@Value("${json.file.path}")
	private String filePath;

	public IdentityService(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public String getJsonStructure() throws StreamReadException, DatabindException, IOException {
		ClassPathResource resource = new ClassPathResource(filePath);

		Object json = objectMapper.readValue(resource.getInputStream(), Object.class);
		return objectMapper.writeValueAsString(json);
	}

	public List<Identity> getAllIdentities() throws StreamReadException, DatabindException, IOException {
		ClassPathResource resource = new ClassPathResource(filePath);

		IdentityContainer identityContainer = objectMapper.readValue(resource.getInputStream(),
				IdentityContainer.class);

		return new ArrayList<>(identityContainer.getIdentities().values());
	}
	
	public void addIdentityToJson(Identity identity) throws StreamWriteException, DatabindException, IOException {
		List<Identity> existingIdentities = readIdentitiesFromFile();

		boolean identityExists = existingIdentities.stream()
				.anyMatch(existingIdentity -> existingIdentity.getId().equals(identity.getId()));		

		if (!identityExists) {
			existingIdentities.add(identity);
			writeIdentitiesToFile(existingIdentities);
		} else {
			throw new IOException("Identity exists already");
		}
	}

	public void addIdentitiesToJson(Map<String, Identity> identities)
			throws StreamWriteException, DatabindException, IOException {
		List<Identity> existingIdentities = readIdentitiesFromFile();

		for (Identity identity : identities.values()) {
			if (identities.containsKey(identity.getId()) == false) {
				throw new IOException("Invalid ID for wrapper and wrapped identity objects");
			}

			boolean identityExists = existingIdentities.stream()
					.anyMatch(existingIdentity -> existingIdentity.getId().equals(identity.getId()));

			if (!identityExists) {
				existingIdentities.add(identity);
				writeIdentitiesToFile(existingIdentities);
			} else {
				throw new IOException("Identity exists already");
			}
		}
	}

	public String removeIdentityFromJson(String identityId) throws StreamReadException, DatabindException, IOException {
		ClassPathResource resource = new ClassPathResource(filePath);
		IdentityContainer identityContainer = objectMapper.readValue(resource.getInputStream(),
				IdentityContainer.class);

		Map<String, Identity> identityMap = identityContainer.getIdentities();
		boolean isRemoved = identityMap.entrySet().removeIf(identity -> identity.getKey().equals(identityId));

		objectMapper.writeValue(resource.getFile(), identityContainer);

		if (isRemoved)
			return identityId;
		else
			throw new IOException("Identity does not exist");
	}
	
	public void removeIdentitiesFromJson(List<String> identityIds) throws StreamWriteException, DatabindException, IOException	{		
		ClassPathResource resource = new ClassPathResource(filePath);
		IdentityContainer identityContainer = objectMapper.readValue(resource.getInputStream(),
				IdentityContainer.class);

		Map<String, Identity> identityMap = identityContainer.getIdentities();
		
		for(String identity: identityIds)	{
			if(identityMap.entrySet().removeIf(id -> id.getKey().equals(identity)) == false)
				throw new IOException("Identities do not exist");
		}
		objectMapper.writeValue(resource.getFile(), identityContainer);
	}

	public String roleOperation(Map<String, RoleOperation> roleOperation)
			throws StreamReadException, DatabindException, IOException {
		ClassPathResource resource = new ClassPathResource(filePath);
		IdentityContainer identityContainer = objectMapper.readValue(resource.getInputStream(),
				IdentityContainer.class);

		Map<String, Identity> identityMap = identityContainer.getIdentities();
		identityMap.forEach((iKey, i) -> {
			roleOperation.forEach((roKey, ro) -> {
				if (i.getId().equals(ro.getId())) {
					List<String> roles = i.getRoles();
					
					switch (ro.getOperation()) {
					case "addRole":
						for(String role: ro.getRoles())	{
							roles.add(role);
						}
						i.setRoles(roles);
						break;
					case "removeRole":
						for(String role: ro.getRoles())
							roles.remove(role);
						i.setRoles(roles);
						break;
					default:
						new IOException("Error conducting role operations");
					}
				}
			});
		});
		
		objectMapper.writeValue(resource.getFile(), identityContainer);

		return "Roles updated";
	}

	/* Helper methods begin here */

	public List<Identity> readIdentitiesFromFile() throws StreamReadException, DatabindException, IOException {
		System.out.println(filePath);
		ClassPathResource resource = new ClassPathResource(filePath);
		System.out.println(resource.getURI());
		IdentityContainer identityContainer = objectMapper.readValue(resource.getInputStream(),
				IdentityContainer.class);

		return new ArrayList<>(identityContainer.getIdentities().values());
	}

	public void writeIdentitiesToFile(List<Identity> identities)
			throws StreamWriteException, DatabindException, IOException {
		ClassPathResource resource = new ClassPathResource(filePath);
		IdentityContainer identityContainer = objectMapper.readValue(resource.getInputStream(),
				IdentityContainer.class);

		Map<String, Identity> identityMap = identityContainer.getIdentities();
		for (Identity identity : identities) {
			identityMap.put(identity.getId(), identity);
		}

		System.out.println("Modified identity container: " + identityContainer.getIdentities());
		System.out.println("resource.getFile() " + resource.getFile());

		objectMapper.writeValue(resource.getFile(), identityContainer);
	}

}
