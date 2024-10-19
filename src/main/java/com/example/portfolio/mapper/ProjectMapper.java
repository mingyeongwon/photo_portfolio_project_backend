package com.example.portfolio.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.example.portfolio.dto.ProjectCreateDto;
import com.example.portfolio.dto.ProjectUpdateDto;
import com.example.portfolio.model.Project;

@Mapper(componentModel = "spring",
	    unmappedTargetPolicy = ReportingPolicy.IGNORE,
	    unmappedSourcePolicy = ReportingPolicy.IGNORE
	)
public interface ProjectMapper {
	ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);


	// Create Dto -> Entity
	@Mapping(source = "categoryId", target = "category.id")
    @Mapping(source = "subcategoryId", target = "subCategory.id")
	Project createDtoToProject(ProjectCreateDto projectToCreateDto);

	// Update Dto -> Entity
	@Mapping(source = "categoryId", target = "category.id")
    @Mapping(source = "subcategoryId", target = "subCategory.id")
	@Mapping(target = "createdAt", expression = "java(new java.util.Date())")
	Project upadateDtoToProject(ProjectUpdateDto projectToUpdateDto);
}
