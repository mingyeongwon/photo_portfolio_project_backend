package com.example.portfolio.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.portfolio.model.Admin;
import com.example.portfolio.repository.AdminRepository;
import com.example.portfolio.service.AdminService;

@RestController
public class LoginController {
	@Autowired
	private AdminRepository adminRepository;
	private AdminService adminService;
	private PasswordEncoder passwordEncoder;
	
	private Logger log = LoggerFactory.getLogger(getClass());

	public LoginController(AdminService adminService) {
		this.adminService = adminService;
	}
	
	@PostMapping("/signUp")
	public String signUpAdmin(@RequestBody Admin admin) {
		adminService.signUpAdmin(admin);
		return "회원가입 성공";
	}
}
