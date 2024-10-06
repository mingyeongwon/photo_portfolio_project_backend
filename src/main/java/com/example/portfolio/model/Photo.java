package com.example.portfolio.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Photo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "image_url")
    private String imageUrl;
	
	@Column(name = "project_id")
    private Long projectId;
	
	private String imgoname;
	private String imgtype;
	
	public Photo() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getImageUrl() {
		return imageUrl;
	}


	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	
	public String getTimgoname() {
		return imgoname;
	}
	
	public void setTimgoname(String imgoname) {
		this.imgoname = imgoname;
	}
	
	public String getTimgtype() {
		return imgtype;
	}
	
	public void setTimgtype(String imgtype) {
		this.imgtype = imgtype;
	}
	
	// hash비교를 위해서 재구성
	@Override
	public int hashCode() {
		return Objects.hash(imgoname, imgtype, projectId);
	}
	
	// 동등성 비교를 위해서 재구성
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Photo other = (Photo) obj;
		return Objects.equals(imgoname, other.imgoname) && Objects.equals(imgtype, other.imgtype)
				&& Objects.equals(projectId, other.projectId);
	}
	
}
