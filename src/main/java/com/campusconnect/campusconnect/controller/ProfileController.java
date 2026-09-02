package com.campusconnect.campusconnect.controller;

import com.campusconnect.campusconnect.dto.response.ProjectResponse;
import com.campusconnect.campusconnect.model.User;
import com.campusconnect.campusconnect.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private ProjectService projectService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getProfile() {
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        User user = (User) principal;

        List<ProjectResponse> myProjects =
                projectService.getMyProjects();

        Map<String, Object> profile = new HashMap<>();
        profile.put("name", user.getName());
        profile.put("email", user.getEmail());
        profile.put("role", user.getRole().name());
        profile.put("totalProjects", myProjects.size());
        profile.put("projects", myProjects);

        return ResponseEntity.ok(profile);
    }
}