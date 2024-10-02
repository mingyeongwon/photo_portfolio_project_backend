package com.example.portfolio.model;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class Project {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;
	private Long view = 0L;

	@CreationTimestamp
	private Date created_at;

	@OneToOne
	@JoinColumn(name = "category_id")
	private Category category;

	public Project() {
		super();
	}

	public Project(Long id, String title, Long view, Date created_at, Category category) {
		super();
		this.id = id;
		this.title = title;
		this.view = view;
		this.created_at = created_at;
		this.category = category;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getView() {
		return view;
	}

	public void setView(Long view) {
		this.view = view;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	@Override
	public String toString() {
		return "Project [id=" + id + ", title=" + title + ", view=" + view + ", created_at=" + created_at
				+ ", category=" + category + "]";
	}
}
