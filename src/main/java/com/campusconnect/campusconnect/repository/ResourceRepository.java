package com.campusconnect.campusconnect.repository;

import com.campusconnect.campusconnect.model.Project;
import com.campusconnect.campusconnect.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    List<Resource> findByProject(Project project);

    List<Resource> findByProjectAndType(Project project, Resource.ResourceType type);
}