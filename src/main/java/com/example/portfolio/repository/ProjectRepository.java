package com.example.portfolio.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.portfolio.dto.ProjectListDto;
import com.example.portfolio.model.Project;


@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>{
	@Query("SELECT new com.example.portfolio.dto.ProjectListDto(p.id, p.title, p.thumbnailUrl, p.createdAt, NULL, c.name, NULL) "
			+ "FROM Project p JOIN p.category c "
			+ "where c.id= :categoryId ")
	Slice<ProjectListDto> findByCategory_id(Pageable pageable,@Param("categoryId") Long categoryId);
	
	@Query("SELECT new com.example.portfolio.dto.ProjectListDto(p.id, p.title, p.thumbnailUrl, p.createdAt, NULL, s.name, NULL) "
			+ "FROM Project p JOIN p.subCategory s "
			+ "where s.id= :subCategoryId " 
)
	Slice<ProjectListDto> findBySubCategory_id(Pageable pageable,@Param("subCategoryId") Long subCategoryId);

	@Query("SELECT new com.example.portfolio.dto.ProjectListDto(p.id, p.title, p.thumbnailUrl, p.createdAt, NULL, c.name, " 
			+"(SELECT COUNT(ph) FROM Photo ph WHERE ph.projectId = p.id)) "
			+"FROM Project p JOIN p.category c "
		    +"WHERE p.title LIKE (CONCAT('%', :keyWord, '%')) ")
	Page<ProjectListDto> findByKeyWord(Pageable pageable,@Param("keyWord") String keyWord);
	
	
	
	 
	 
}
 