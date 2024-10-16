package com.example.portfolio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.portfolio.model.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
	List<Project> findByCategory_Id(Long categoryId);

	List<Project> findBySubCategory_Id(Long subCategory);

	// 카테고리 사용 유무 확인
	boolean existsByCategory_Id(Long Categoryid);
	// 서브 카테고리 사용 유무
	boolean existsBySubCategory_Id(Long subCategoryId);

}
