package com.example.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.portfolio.model.Project;

public interface ProjectRepository extends JpaRepository<Project, Long>{

}
