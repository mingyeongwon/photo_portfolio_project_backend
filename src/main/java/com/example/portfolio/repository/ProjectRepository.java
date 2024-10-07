package com.example.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.portfolio.model.Project;
import java.util.List;


@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>{
	 List<Project> findByCategory_Id(Long categoryId);
}
