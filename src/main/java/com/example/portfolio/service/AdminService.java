package com.example.portfolio.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.portfolio.model.Admin;
import com.example.portfolio.repository.AdminRepository;

@Service
public class AdminService {
	
    private AdminRepository adminRepository;
    private PasswordEncoder passwordEncoder; 

    public AdminService(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }
	
    // 회원가입
	public void signUpAdmin(Admin admin) {
		// 비밀번호 암호화
		admin.setPassword(passwordEncoder.encode(admin.getPassword()));
		adminRepository.save(admin);
	}

}
