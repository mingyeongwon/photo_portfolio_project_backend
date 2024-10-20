package com.example.portfolio.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Project {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;
	private int view = 0;
	
	@Column(name = "thumbnail_url")
    private String thumbnailUrl;

	@CreationTimestamp
	@JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="Asia/Seoul") //날짜 포멧 바꾸기
	@Column(name = "created_at")
	private Date createdAt;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Photo> photos = new ArrayList<>();
	
	public List<Photo> getPhotos() {
		return photos;
	}

	public void setPhotos(List<Photo> photos) {
		this.photos = photos;
	}

	@ManyToOne
	@JoinColumn(name = "sub_category_id")
	private SubCategory subCategory;
	
	public Project() {
		super();
	}

	public Project(Long id, String title, int view, Date createdAt, String thumbnailUrl, Category category,SubCategory subCategory) {
		super();
		this.id = id;
		this.title = title;
		this.view = view;
		this.createdAt = createdAt;
		this.thumbnailUrl = thumbnailUrl;
		this.category = category;
		this.subCategory = subCategory;
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

	public int getView() {
		return view;
	}

	public void setView(int view) {
		this.view = view;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	
	public String getThumbnailUrl() {
	  return thumbnailUrl;
	}
	
	public void setThumbnailUrl(String thumbnailUrl) {
	  this.thumbnailUrl = thumbnailUrl;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public SubCategory getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(SubCategory subCategory) {
		this.subCategory = subCategory;
	}

	@Override
	public String toString() {
		return "Project [id=" + id + ", title=" + title + ", view=" + view + ", created_at=" + createdAt
				+ ", category=" + category + "]";
	}
}