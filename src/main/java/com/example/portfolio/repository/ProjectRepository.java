package com.example.portfolio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.portfolio.model.Project;


@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>{
	 List<Project> findByCategory_Id(Long categoryId);
	 
	 @Query("SELECT p FROM Project p JOIN SubCategory sc ON p.id = sc.projectId WHERE sc.id = :subCategoryId")
	 List<Project> findProjectsBySubCategory(@Param("subCategoryId") Long subCategoryId);
}
