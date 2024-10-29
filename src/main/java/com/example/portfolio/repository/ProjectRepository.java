package com.example.portfolio.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.portfolio.dto.ProjectDetailDto;
import com.example.portfolio.dto.ProjectListDto;
import com.example.portfolio.model.Category;
import com.example.portfolio.model.Project;


@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>{

	   @Query("SELECT new com.example.portfolio.dto.ProjectListDto(" +
	           "p.id, p.title, p.thumbnailUrl, p.createdAt, p.view, " +
	           "p.category.name, p.subCategory.name, NULL) " +
	           "FROM Project p ")
	    Slice<ProjectListDto> findAllProject(Pageable pageable);

	    @Query("SELECT new com.example.portfolio.dto.ProjectListDto(" +
	           "p.id, p.title, p.thumbnailUrl, p.createdAt, p.view, " +
	           "p.category.name, p.subCategory.name, NULL) " +
	           "FROM Project p " +
	           "WHERE p.category.id = :categoryId")
	    Slice<ProjectListDto> findByCategory_id(Pageable pageable, @Param("categoryId") Long categoryId);

	    @Query("SELECT new com.example.portfolio.dto.ProjectListDto(" +
	           "p.id, p.title, p.thumbnailUrl, p.createdAt, p.view, " +
	           "p.category.name, p.subCategory.name, NULL) " +
	           "FROM Project p " +
	           "WHERE p.subCategory.id = :subCategoryId")
	    Slice<ProjectListDto> findBySubCategory_id(Pageable pageable, @Param("subCategoryId") Long subCategoryId);

	@Query("SELECT new com.example.portfolio.dto.ProjectListDto(p.id, p.title, p.thumbnailUrl, p.createdAt, p.view, p.category.name ,p.subCategory.name, COUNT(ph) as imageCount) "
	        + "FROM Project p "
	        + "LEFT JOIN Photo ph ON ph.projectId = p.id "
	        + "WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyWord, '%')) "
	        + "GROUP BY p.id,p.category.name ,p.subCategory.name")
	Page<ProjectListDto> findByKeyWord(Pageable pageable, @Param("keyWord") String keyWord);
	
	@Query("SELECT DISTINCT p.category FROM Project p WHERE p.category IS NOT NULL")
	List<Category> findCategoriesWithProjects();

	
	List<Project> findByCategory_Id(Long categoryId);

	List<Project> findBySubCategory_Id(Long subCategory);

	// 카테고리 사용 유무 확인
	boolean existsByCategory_Id(Long Categoryid);

	// 서브 카테고리 사용 유무
	boolean existsBySubCategory_Id(Long subCategoryId);
	
	
	@Modifying
	@Transactional
	@Query("UPDATE Project p SET p.view = p.view + 1 WHERE p.id = :projectId")
	void updateViewCount(@Param("projectId") Long projectId);
	
	@Query("SELECT new com.example.portfolio.dto.ProjectListDto(p.id, p.title, p.thumbnailUrl, p.createdAt, p.view, p.category.name,  p.subCategory.name, NULL) "
			+ "FROM Project p "
			+ "where p.id= :projectId ")
	List<ProjectListDto> findByProjectId(@Param("projectId") Long projectId);
	
	
	// 프로젝트 detail 정보 가져오기
	@Query("SELECT new com.example.portfolio.dto.ProjectDetailDto(p.id, p.title, p.thumbnailUrl, c.id, s.id) "
			+ "FROM Project p "
			+ "JOIN p.category c "
			+ "JOIN p.subCategory s "
			+ "where p.id= :projectId ")
	ProjectDetailDto findProjectDetailByProjectId(@Param("projectId") Long projectId);
	
}
 