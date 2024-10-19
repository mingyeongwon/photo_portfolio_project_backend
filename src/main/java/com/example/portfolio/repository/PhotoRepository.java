package com.example.portfolio.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.portfolio.dto.PhotoListDto;
import com.example.portfolio.model.Photo;

public interface PhotoRepository extends JpaRepository<Photo, Long>{
	List<Photo> findByProjectId(Long id);
	List<Photo> findAllByProjectId(Long id);
	
	@Query("SELECT new com.example.portfolio.dto.PhotoListDto(ph.id, ph.imageUrl, p.title)"
			+ " FROM Photo ph, Project p WHERE ph.projectId=p.id and ph.projectId = :projectId")
	Slice<PhotoListDto> findByPhotosProjectId(@Param("projectId") Long projectId, Pageable pageable);

}
