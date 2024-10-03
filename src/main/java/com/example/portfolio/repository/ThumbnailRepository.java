package com.example.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.portfolio.model.Thumbnail;

public interface ThumbnailRepository extends JpaRepository<Thumbnail, Long>{

}
