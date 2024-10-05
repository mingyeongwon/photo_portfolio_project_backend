package com.example.portfolio.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.portfolio.model.Admin;
import com.example.portfolio.repository.AdminRepository;

@Service
public class AdminDetailsService implements UserDetailsService {
	@Autowired
	private AdminRepository adminRepository;
	
	// 사용자의 이름을 매개변수로 받아서, 해당 사용자의 정보를 포함한 UserDetails 객체 반환
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Admin admin = adminRepository.findById(username)
									// 에러 문구 출력 X
									.orElseThrow(() -> new UsernameNotFoundException("없는 회원 입니다"));
		
		return new AdminDetails(admin);
	}
}
