package com.campusconnect.campusconnect.service;

import com.campusconnect.campusconnect.dto.response.ApiResponse;
import com.campusconnect.campusconnect.dto.response.TeamMemberResponse;
import com.campusconnect.campusconnect.model.Project;
import com.campusconnect.campusconnect.model.TeamMember;
import com.campusconnect.campusconnect.model.User;
import com.campusconnect.campusconnect.repository.ProjectRepository;
import com.campusconnect.campusconnect.repository.TeamMemberRepository;
import com.campusconnect.campusconnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamService {

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        }
        return userRepository.findByEmail(principal.toString())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private TeamMemberResponse toResponse(TeamMember member) {
        return TeamMemberResponse.builder()
                .id(member.getId())
                .userName(member.getUser().getName())
                .userEmail(member.getUser().getEmail())
                .status(member.getStatus().name())
                .requestedAt(member.getRequestedAt())
                .respondedAt(member.getRespondedAt())
                .build();
    }

    public ApiResponse requestToJoin(Long projectId) {
        User currentUser = getCurrentUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (project.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are the owner of this project");
        }

        if (teamMemberRepository.existsByProjectAndUser(project, currentUser)) {
            throw new RuntimeException("You have already requested to join");
        }

        int currentMembers = teamMemberRepository
                .findByProjectAndStatus(project, TeamMember.Status.ACCEPTED)
                .size();

        if (currentMembers >= project.getMaxTeamSize()) {
            throw new RuntimeException("Team is full. Max size is "
                    + project.getMaxTeamSize());
        }

        TeamMember member = TeamMember.builder()
                .project(project)
                .user(currentUser)
                .build();

        teamMemberRepository.save(member);

        return ApiResponse.builder()
                .success(true)
                .message("Join request sent successfully")
                .build();
    }

    public List<TeamMemberResponse> getJoinRequests(Long projectId) {
        User currentUser = getCurrentUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Only project owner can view requests");
        }

        return teamMemberRepository
                .findByProjectAndStatus(project, TeamMember.Status.PENDING)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<TeamMemberResponse> getTeamMembers(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        return teamMemberRepository
                .findByProjectAndStatus(project, TeamMember.Status.ACCEPTED)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ApiResponse respondToRequest(Long memberId, String decision) {
        User currentUser = getCurrentUser();

        TeamMember member = teamMemberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        Project project = member.getProject();

        if (!project.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Only project owner can respond to requests");
        }

        if (!member.getStatus().equals(TeamMember.Status.PENDING)) {
            throw new RuntimeException("Request already responded to");
        }

        if (decision.equalsIgnoreCase("ACCEPT")) {

            int currentMembers = teamMemberRepository
                    .findByProjectAndStatus(
                            member.getProject(),
                            TeamMember.Status.ACCEPTED)
                    .size();

            if (currentMembers >= member.getProject().getMaxTeamSize()) {
                throw new RuntimeException("Team is full. Cannot accept more members");
            }

            member.setStatus(TeamMember.Status.ACCEPTED);

        } else if (decision.equalsIgnoreCase("REJECT")) {
            member.setStatus(TeamMember.Status.REJECTED);
        } else {
            throw new RuntimeException("Invalid decision. Use ACCEPT or REJECT");
        }

        member.setRespondedAt(LocalDateTime.now());
        teamMemberRepository.save(member);

        return ApiResponse.builder()
                .success(true)
                .message("Request " + decision.toLowerCase() + "ed successfully")
                .build();
    }

    public ApiResponse leaveProject(Long projectId) {
        User currentUser = getCurrentUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        TeamMember member = teamMemberRepository
                .findByProjectAndUser(project, currentUser)
                .orElseThrow(() -> new RuntimeException("You are not a member of this project"));

        teamMemberRepository.delete(member);

        return ApiResponse.builder()
                .success(true)
                .message("Left project successfully")
                .build();
    }

    public List<TeamMemberResponse> getMyJoinedProjects() {
        User currentUser = getCurrentUser();
        return teamMemberRepository.findByUser(currentUser)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}