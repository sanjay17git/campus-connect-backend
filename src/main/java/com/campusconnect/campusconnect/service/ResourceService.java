package com.campusconnect.campusconnect.service;

import com.campusconnect.campusconnect.dto.request.ResourceRequest;
import com.campusconnect.campusconnect.dto.response.ApiResponse;
import com.campusconnect.campusconnect.dto.response.ResourceResponse;
import com.campusconnect.campusconnect.model.Project;
import com.campusconnect.campusconnect.model.Resource;
import com.campusconnect.campusconnect.model.TeamMember;
import com.campusconnect.campusconnect.model.User;
import com.campusconnect.campusconnect.repository.ProjectRepository;
import com.campusconnect.campusconnect.repository.ResourceRepository;
import com.campusconnect.campusconnect.repository.TeamMemberRepository;
import com.campusconnect.campusconnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

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

    private ResourceResponse toResponse(Resource resource) {
        return ResourceResponse.builder()
                .id(resource.getId())
                .title(resource.getTitle())
                .url(resource.getUrl())
                .type(resource.getType().name())
                .uploadedByName(resource.getUploadedBy().getName())
                .projectId(resource.getProject().getId())
                .uploadedAt(resource.getUploadedAt())
                .build();
    }

    public ResourceResponse addResource(ResourceRequest request) {
        User currentUser = getCurrentUser();

        Project project = projectRepository
                .findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        boolean isOwner = project.getOwner().getId()
                .equals(currentUser.getId());
        boolean isAcceptedMember = teamMemberRepository
                .findByProjectAndUser(project, currentUser)
                .map(m -> m.getStatus() == TeamMember.Status.ACCEPTED)
                .orElse(false);

        if (!isOwner && !isAcceptedMember) {
            throw new RuntimeException(
                    "Only owner or accepted members can add resources");
        }

        Resource resource = Resource.builder()
                .title(request.getTitle())
                .url(request.getUrl())
                .type(Resource.ResourceType.valueOf(request.getType()))
                .project(project)
                .uploadedBy(currentUser)
                .build();

        resourceRepository.save(resource);
        return toResponse(resource);
    }

    public List<ResourceResponse> getProjectResources(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        return resourceRepository.findByProject(project)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ResourceResponse> getResourcesByType(
            Long projectId, String type) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        return resourceRepository
                .findByProjectAndType(project,
                        Resource.ResourceType.valueOf(type))
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ApiResponse deleteResource(Long resourceId) {
        User currentUser = getCurrentUser();

        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        boolean isOwner = resource.getProject().getOwner().getId()
                .equals(currentUser.getId());
        boolean isUploader = resource.getUploadedBy().getId()
                .equals(currentUser.getId());

        if (!isOwner && !isUploader) {
            throw new RuntimeException(
                    "Only project owner or uploader can delete this resource");
        }

        resourceRepository.delete(resource);
        return ApiResponse.builder()
                .success(true)
                .message("Resource deleted successfully")
                .build();
    }
}