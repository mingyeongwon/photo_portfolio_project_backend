package com.example.portfolio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.portfolio.repository.ProjectRepository;

@Service
public class ProjectService {
	
	// 여기서 생성자 주입을 하지 않고 필드 주입을 하는게 좋은건지?
	@Autowired
	private ProjectRepository projectRepository;

}
