package com.example.portfolio.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.portfolio.repository.AdminRepository;

@Controller
public class LoginController {
	@Autowired
	private AdminRepository adminRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	public LoginController(AdminRepository adminRepository) {
		this.adminRepository = adminRepository;
	}
	
	@PostMapping("/Login")
	public Map<String, String> adminLogin(String id, String password) {
		
		Map<String, String> map = new HashMap<>();
		
//		String encodedPassword = 
//		boolean checkPasswordResult = passwordEncoder.matches(password, password);
//		
//		if(checkPasswordResult) {
//			map.put("result", "success");
//			map.put("id", "id");
//		} else {
//			map.put("result", "fail");
//		}
//		
		return map;
	}
}
