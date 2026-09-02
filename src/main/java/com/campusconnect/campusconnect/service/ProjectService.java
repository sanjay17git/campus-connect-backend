package com.campusconnect.campusconnect.service;


import com.campusconnect.campusconnect.dto.request.ProjectRequest;
import com.campusconnect.campusconnect.dto.response.ApiResponse;
import com.campusconnect.campusconnect.dto.response.ProjectResponse;
import com.campusconnect.campusconnect.model.Like;
import com.campusconnect.campusconnect.model.Project;
import com.campusconnect.campusconnect.model.Task;
import com.campusconnect.campusconnect.model.User;
import com.campusconnect.campusconnect.repository.LikeRepository;
import com.campusconnect.campusconnect.repository.ProjectRepository;
import com.campusconnect.campusconnect.repository.TaskRepository;
import com.campusconnect.campusconnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LikeRepository likeRepository;

    private User getCurrentUser() {
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        System.out.println("DEBUG PRINCIPAL: " + principal);

        // Principal is already the User object set in JwtAuthFilter
        if (principal instanceof User) {
            return (User) principal;
        }

        // Fallback — try email string
        return userRepository.findByEmail(principal.toString())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private ProjectResponse toResponse(Project project) {
        // Get task counts
        List<Task> allTasks = taskRepository
                .findByProject(project);
        int totalTasks = allTasks.size();
        int completedTasks = (int) allTasks.stream()
                .filter(t -> t.getStatus() == Task.Status.DONE)
                .count();
        int percentage = totalTasks == 0 ? 0 :
                (completedTasks * 100) / totalTasks;

        return ProjectResponse.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .status(project.getStatus().name())
                .tags(project.getTags())
                .likesCount(project.getLikesCount())
                .ownerName(project.getOwner().getName())
                .ownerEmail(project.getOwner().getEmail())
                .maxTeamSize(project.getMaxTeamSize())
                .createdAt(project.getCreatedAt())
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .completionPercentage(percentage)
                .build();
    }

    public ProjectResponse createProject(ProjectRequest request) {
        User owner = getCurrentUser();
        Project project = Project.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status((Project.Status.valueOf(request.getStatus())))
                .tags(request.getTags())
                .owner(owner)
                .likesCount(0)
                .maxTeamSize(request.getMaxTeamSize())
                .build();
        projectRepository.save(project);
        return toResponse(project);
    }
    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    public List<ProjectResponse> getMyProjects() {
        User user = getCurrentUser();
        return projectRepository.findByOwner(user)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        return toResponse(project);
    }

    public ProjectResponse updateProject(Long id, ProjectRequest request) {
        User currentUser = getCurrentUser();

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not the owner of this project");
        }

        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());
        project.setStatus(Project.Status.valueOf(request.getStatus()));
        project.setTags(request.getTags());

        projectRepository.save(project);
        return toResponse(project);
    }

    public ApiResponse deleteProject(Long id) {
        User currentUser = getCurrentUser();

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not the owner of this project");
        }

        projectRepository.delete(project);
        return ApiResponse.builder()
                .success(true)
                .message("Project deleted successfully")
                .build();
    }

    public List<ProjectResponse> searchProjects(String keyword) {
        return projectRepository.findByTitleContainingIgnoreCase(keyword)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ProjectResponse> filterByStatus(String status) {
        return projectRepository.findByStatus(Project.Status.valueOf(status))
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ApiResponse toggleLike(Long projectId) {
        User currentUser = getCurrentUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (likeRepository.existsByProjectAndUser(project, currentUser)) {
            Like like = likeRepository
                    .findByProjectAndUser(project, currentUser)
                    .orElseThrow();
            likeRepository.delete(like);
            project.setLikesCount(project.getLikesCount() - 1);
            projectRepository.save(project);
            return ApiResponse.builder()
                    .success(true)
                    .message("Project unliked")
                    .build();
        } else {
            Like like = Like.builder()
                    .project(project)
                    .user(currentUser)
                    .build();
            likeRepository.save(like);
            project.setLikesCount(project.getLikesCount() + 1);
            projectRepository.save(project);
            return ApiResponse.builder()
                    .success(true)
                    .message("Project liked")
                    .build();
        }
    }
}
