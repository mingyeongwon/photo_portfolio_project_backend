package com.example.portfolio.security;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.example.portfolio.model.Admin;

public class AdminDetails extends User {

    private Admin admin;

    public AdminDetails(Admin admin) {
    	super(admin.getId(), admin.getPassword(), 
    			Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")));
        this.admin = admin;
    }
    
    public Admin getAdmin() {
    	return admin;
    }
}
