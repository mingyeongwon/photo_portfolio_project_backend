package com.example.portfolio.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.portfolio.dto.ProjectListCustomDto;
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
	public ProjectListCustomDto getAdminProjectList(Pageable pageable, String keyWord) {
		Page<ProjectListDto> projectListDto =projectRepository.findByKeyWord(pageable, keyWord);
		ProjectListCustomDto pojectListCustomDto = new ProjectListCustomDto();
		pojectListCustomDto.setContent(projectListDto.getContent());
		pojectListCustomDto.setTotalPages(projectListDto.getTotalPages());
	    return pojectListCustomDto;
	}

}
