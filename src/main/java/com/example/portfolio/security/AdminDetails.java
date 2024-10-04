package com.example.portfolio.security;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.example.portfolio.model.Admin;

public class AdminDetails extends User {

    private Admin admin;

    public AdminDetails(Admin admin) {
    	super(admin.getId(), admin.getPassword(), 
    			// Collections.singleton() : Collection에 권한을 1개만 주는 방법
    			// SimpleGrantedAuthority() : 사용자 권한 설정 
    			Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")));
        this.admin = admin;
    }
    
    public Admin getAdmin() {
    	return admin;
    }
}
