package com.campusconnect.campusconnect.repository;

import com.campusconnect.campusconnect.model.Project;
import com.campusconnect.campusconnect.model.TeamMember;
import com.campusconnect.campusconnect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    List<TeamMember> findByProject(Project project);

    List<TeamMember> findByProjectAndStatus(Project project, TeamMember.Status status);

    List<TeamMember> findByUser(User user);

    Optional<TeamMember> findByProjectAndUser(Project project, User user);

    Boolean existsByProjectAndUser(Project project, User user);
}