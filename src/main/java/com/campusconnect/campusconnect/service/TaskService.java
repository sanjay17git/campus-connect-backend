package com.campusconnect.campusconnect.service;

import com.campusconnect.campusconnect.dto.request.TaskRequest;
import com.campusconnect.campusconnect.dto.request.TaskStatusRequest;
import com.campusconnect.campusconnect.dto.response.ApiResponse;
import com.campusconnect.campusconnect.dto.response.TaskResponse;
import com.campusconnect.campusconnect.model.Project;
import com.campusconnect.campusconnect.model.Task;
import com.campusconnect.campusconnect.model.TeamMember;
import com.campusconnect.campusconnect.model.User;
import com.campusconnect.campusconnect.repository.ProjectRepository;
import com.campusconnect.campusconnect.repository.TaskRepository;
import com.campusconnect.campusconnect.repository.TeamMemberRepository;
import com.campusconnect.campusconnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    // Helper — get current logged in user
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

    // Helper — convert Task to TaskResponse DTO
    private TaskResponse toResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus().name())
                .assignedToName(task.getAssignedTo() != null
                        ? task.getAssignedTo().getName()
                        : "Unassigned")
                .projectId(task.getProject().getId())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    // Create task — only owner or accepted member can create
    public TaskResponse createTask(TaskRequest request) {
        User currentUser = getCurrentUser();

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Check if current user is owner or accepted member
        boolean isOwner = project.getOwner().getId()
                .equals(currentUser.getId());
        boolean isMember = teamMemberRepository
                .existsByProjectAndUser(project, currentUser);

        if (!isOwner && !isMember) {
            throw new RuntimeException(
                    "Only project owner or members can create tasks");
        }

        // If assignedToUserId provided — validate they are accepted member
        User assignedTo = null;
        if (request.getAssignedToUserId() != null) {
            assignedTo = userRepository
                    .findById(request.getAssignedToUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Verify assigned user is accepted member or owner
            boolean assigneeIsOwner = project.getOwner().getId()
                    .equals(assignedTo.getId());
            boolean assigneeIsMember = teamMemberRepository
                    .findByProjectAndUser(project, assignedTo)
                    .map(m -> m.getStatus() == TeamMember.Status.ACCEPTED)
                    .orElse(false);

            if (!assigneeIsOwner && !assigneeIsMember) {
                throw new RuntimeException(
                        "Task can only be assigned to accepted team members");
            }
        }

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .project(project)
                .assignedTo(assignedTo)
                .build();

        taskRepository.save(task);
        return toResponse(task);
    }

    // Get all tasks of a project
    public List<TaskResponse> getProjectTasks(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        return taskRepository.findByProject(project)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Get tasks by status for a project
    public List<TaskResponse> getTasksByStatus(Long projectId, String status) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        return taskRepository
                .findByProjectAndStatus(project, Task.Status.valueOf(status))
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Get my assigned tasks
    public List<TaskResponse> getMyTasks() {
        User currentUser = getCurrentUser();
        return taskRepository.findByAssignedTo(currentUser)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Update task status — assigned member or owner can update
    public TaskResponse updateTaskStatus(Long taskId,
                                         TaskStatusRequest request) {
        User currentUser = getCurrentUser();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        Project project = task.getProject();

        // Only owner or assigned member can update status
        boolean isOwner = project.getOwner().getId()
                .equals(currentUser.getId());
        boolean isAssigned = task.getAssignedTo() != null
                && task.getAssignedTo().getId().equals(currentUser.getId());

        if (!isOwner && !isAssigned) {
            throw new RuntimeException(
                    "Only owner or assigned member can update task status");
        }

        task.setStatus(Task.Status.valueOf(request.getStatus()));
        taskRepository.save(task);
        return toResponse(task);
    }

    // Delete task — only owner can delete
    public ApiResponse deleteTask(Long taskId) {
        User currentUser = getCurrentUser();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getProject().getOwner().getId()
                .equals(currentUser.getId())) {
            throw new RuntimeException(
                    "Only project owner can delete tasks");
        }

        taskRepository.delete(task);
        return ApiResponse.builder()
                .success(true)
                .message("Task deleted successfully")
                .build();
    }
}