package com.example.portfolio.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.portfolio.dto.PhotoListDto;
import com.example.portfolio.model.Photo;
import com.example.portfolio.model.Project;

public interface PhotoRepository extends JpaRepository<Photo, Long>{
	List<Photo> findByProjectId(Long id);
	List<Photo> findAllByProjectId(Long id);
	
	@Query("SELECT new com.example.portfolio.dto.PhotoListDto(ph.id, ph.imageUrl) "
			+ "FROM Photo ph "
			+ "LEFT JOIN Project p ON p.id = ph.projectId "
			+ "WHERE ph.projectId = :projectId")
	Slice<PhotoListDto> findByPhotosProjectId(@Param("projectId") Long projectId, Pageable pageable);
	
	@Query("SELECT new com.example.portfolio.dto.PhotoListDto(ph.id, ph.imageUrl) " +
		       "FROM Photo ph " +
		       "WHERE ph.projectId = :projectId")
		List<PhotoListDto> findDetailPhotoByProjectId(@Param("projectId") Long projectId);

}
