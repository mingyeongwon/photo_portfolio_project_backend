package com.example.portfolio.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
public class AdminEntity {
	@Id
	private String id;
	private String password;
}
