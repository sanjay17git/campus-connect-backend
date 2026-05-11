package com.campusconnect.campusconnect.controller;

import com.campusconnect.campusconnect.dto.request.ProjectRequest;
import com.campusconnect.campusconnect.dto.response.ApiResponse;
import com.campusconnect.campusconnect.dto.response.ProjectResponse;
import com.campusconnect.campusconnect.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    // Create project
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(projectService.createProject(request));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/my")
    public ResponseEntity<List<ProjectResponse>> getMyProjects() {
        return ResponseEntity.ok(projectService.getMyProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(
            @PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(projectService.updateProject(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteProject(
            @PathVariable Long id) {
        return ResponseEntity.ok(projectService.deleteProject(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProjectResponse>> searchProjects(
            @RequestParam String keyword) {
        return ResponseEntity.ok(projectService.searchProjects(keyword));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ProjectResponse>> filterByStatus(
            @RequestParam String status) {
        return ResponseEntity.ok(projectService.filterByStatus(status));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<ApiResponse> toggleLike(
            @PathVariable Long id) {
        return ResponseEntity.ok(projectService.toggleLike(id));
    }
}