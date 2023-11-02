package com.webapp.genericiamendpoints.genericendpoints.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webapp.genericiamendpoints.genericendpoints.model.Identity;
import com.webapp.genericiamendpoints.genericendpoints.model.RoleOperation;
import com.webapp.genericiamendpoints.genericendpoints.service.IdentityService;

@RestController
@RequestMapping("/api/identity")
public class IdentityController {
	
	@Autowired
	private IdentityService identityService;

	@GetMapping("/structure")
	public ResponseEntity<String> readStructure() {
		try {
			String  jsonString = identityService.getJsonStructure();
			return ResponseEntity.ok().contentType(org.springframework.http.MediaType.APPLICATION_JSON)
					.body(jsonString);
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading JSON file.");
		}
	}

	@GetMapping("/all")
	public ResponseEntity<?> readObjects() {
		try {
	        List<Identity> identities = identityService.getAllIdentities();
	        return ResponseEntity.ok().body(identities);
		} catch (IOException e) {
			return ResponseEntity.status(500).body("Error reading JSON file.");
		}
	}
    
    @PutMapping("/add")
    public ResponseEntity<String> addIdentity(@RequestBody Identity identity) {
        try {
            identityService.addIdentityToJson(identity);
            return ResponseEntity.ok().body("Identity added successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    @PutMapping("/addMany")
    public ResponseEntity<String> addIdentities(@RequestBody Map<String, Identity> identities) {
        try {
            identityService.addIdentitiesToJson(identities);
            return ResponseEntity.ok().body("Identities added successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    @DeleteMapping("/remove")
    public ResponseEntity<String> removeIdentity(@RequestBody String identityId) {
        try {
            identityService.removeIdentityFromJson(identityId);
            return ResponseEntity.ok().body("Identity removed successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    @DeleteMapping("/removeMany")
    public ResponseEntity<String> removeIdentities(@RequestBody List<String> identityIds) {
        try {
            identityService.removeIdentitiesFromJson(identityIds);
            return ResponseEntity.ok().body("Identities removed successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    @PostMapping("")
    public ResponseEntity<String> roleOperation(@RequestBody Map<String, RoleOperation> roleOperation) {
    	try {
			identityService.roleOperation(roleOperation);
    		return ResponseEntity.ok().body("Operation performed successfully.");
		} catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
    }

}
