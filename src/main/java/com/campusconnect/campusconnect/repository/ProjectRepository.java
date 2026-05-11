package com.campusconnect.campusconnect.repository;

import com.campusconnect.campusconnect.model.Project;
import com.campusconnect.campusconnect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByOwner(User owner);

    List<Project> findByStatus(Project.Status status);

    List<Project> findByTitleContainingIgnoreCase(String keyword);
}