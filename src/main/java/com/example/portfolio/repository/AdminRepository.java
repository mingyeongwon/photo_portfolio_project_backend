package com.example.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.portfolio.model.AdminEntity;

public interface AdminRepository extends JpaRepository<AdminEntity, String>{

}
