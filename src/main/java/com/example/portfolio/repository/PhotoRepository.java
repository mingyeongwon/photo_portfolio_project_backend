package com.example.portfolio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.portfolio.model.Photo;

public interface PhotoRepository extends JpaRepository<Photo, Long>{

	List<Photo> findByProjectId(Long id);

}
