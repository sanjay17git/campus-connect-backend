package com.campusconnect.campusconnect.repository;

import com.campusconnect.campusconnect.model.Like;
import com.campusconnect.campusconnect.model.Project;
import com.campusconnect.campusconnect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByProjectAndUser(Project project, User user);

    Boolean existsByProjectAndUser(Project project, User user);

    int countByProject(Project project);
}