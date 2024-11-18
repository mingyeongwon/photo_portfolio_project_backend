package com.example.portfolio.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
	    use = JsonTypeInfo.Id.CLASS,
	    include = JsonTypeInfo.As.PROPERTY,
	    property = "@class"
	)
@JsonIgnoreProperties(ignoreUnknown = true) // JSON에서 존재하지 않는 필드 무시
public class ProjectListDto {
    private Long id;
    private String title;
    private String imageUrl; // 썸네일 URL

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date createdAt;

    private int view;
    private String categoryName;
    private String subCategoryName;
    private Long imageCount;

    // 기본 생성자
    public ProjectListDto() {
    }

    // Jackson에서 역직렬화를 위한 명시적인 생성자
    @JsonCreator
    public ProjectListDto(
        @JsonProperty("id") Long id,
        @JsonProperty("title") String title,
        @JsonProperty("imageUrl") String imageUrl,
        @JsonProperty("createdAt") Date createdAt,
        @JsonProperty("view") int view,
        @JsonProperty("categoryName") String categoryName,
        @JsonProperty("subCategoryName") String subCategoryName,
        @JsonProperty("imageCount") Long imageCount
    ) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.view = view;
        this.categoryName = categoryName;
        this.subCategoryName = subCategoryName;
        this.imageCount = imageCount;
    }

    // Getters and Setters
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public Long getImageCount() {
        return imageCount;
    }

    public void setImageCount(Long imageCount) {
        this.imageCount = imageCount;
    }

    @Override
    public String toString() {
        return "ProjectListDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", createdAt=" + createdAt +
                ", view=" + view +
                ", categoryName='" + categoryName + '\'' +
                ", subCategoryName='" + subCategoryName + '\'' +
                ", imageCount=" + imageCount +
                '}';
    }
}
