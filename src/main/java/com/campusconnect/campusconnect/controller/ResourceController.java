package com.campusconnect.campusconnect.controller;

import com.campusconnect.campusconnect.dto.request.ResourceRequest;
import com.campusconnect.campusconnect.dto.response.ApiResponse;
import com.campusconnect.campusconnect.dto.response.ResourceResponse;
import com.campusconnect.campusconnect.service.ResourceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @PostMapping
    public ResponseEntity<ResourceResponse> addResource(
            @Valid @RequestBody ResourceRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resourceService.addResource(request));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<ResourceResponse>> getProjectResources(
            @PathVariable Long projectId) {
        return ResponseEntity.ok(
                resourceService.getProjectResources(projectId));
    }

    @GetMapping("/project/{projectId}/type")
    public ResponseEntity<List<ResourceResponse>> getResourcesByType(
            @PathVariable Long projectId,
            @RequestParam String type) {
        return ResponseEntity.ok(
                resourceService.getResourcesByType(projectId, type));
    }

    @DeleteMapping("/{resourceId}")
    public ResponseEntity<ApiResponse> deleteResource(
            @PathVariable Long resourceId) {
        return ResponseEntity.ok(
                resourceService.deleteResource(resourceId));
    }
}