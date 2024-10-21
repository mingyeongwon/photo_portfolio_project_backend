package com.example.portfolio.service;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.portfolio.dto.ProjectListDto;
import com.example.portfolio.model.Admin;
import com.example.portfolio.repository.AdminRepository;
import com.example.portfolio.repository.ProjectRepository;

import jakarta.transaction.Transactional;

@Service
public class AdminService {

	private final AdminRepository adminRepository;
	private final PasswordEncoder passwordEncoder;
	private final ProjectRepository projectRepository;

	public AdminService(AdminRepository adminRepository, PasswordEncoder passwordEncoder,ProjectRepository projectRepository) {
		this.adminRepository = adminRepository;
		this.passwordEncoder = passwordEncoder;
		this.projectRepository= projectRepository;
	}

	// 회원가입
	public void signUpAdmin(Admin admin) {
		// 비밀번호 암호화
		admin.setPassword(passwordEncoder.encode(admin.getPassword()));
		adminRepository.save(admin);
	}
	
	//admin page 프로젝트 불러오기 
	@Transactional
	@Cacheable("projects")
	public Page<ProjectListDto> getAdminProjectList(Pageable pageable, String keyWord) {
	    return projectRepository.findByKeyWord(pageable, keyWord);
	}

	public List<ProjectListDto> getAdminProject(Long projectId) {
		return projectRepository.findByProjectId(projectId);
	}

}
