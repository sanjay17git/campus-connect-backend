package com.campusconnect.campusconnect.controller;

import com.campusconnect.campusconnect.dto.request.TaskRequest;
import com.campusconnect.campusconnect.dto.request.TaskStatusRequest;
import com.campusconnect.campusconnect.dto.response.ApiResponse;
import com.campusconnect.campusconnect.dto.response.TaskResponse;
import com.campusconnect.campusconnect.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(taskService.createTask(request));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskResponse>> getProjectTasks(
            @PathVariable Long projectId) {
        return ResponseEntity.ok(taskService.getProjectTasks(projectId));
    }

    @GetMapping("/project/{projectId}/status")
    public ResponseEntity<List<TaskResponse>> getTasksByStatus(
            @PathVariable Long projectId,
            @RequestParam String status) {
        return ResponseEntity.ok(
                taskService.getTasksByStatus(projectId, status));
    }

    @GetMapping("/my")
    public ResponseEntity<List<TaskResponse>> getMyTasks() {
        return ResponseEntity.ok(taskService.getMyTasks());
    }

    @PutMapping("/{taskId}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskStatusRequest request) {
        return ResponseEntity.ok(
                taskService.updateTaskStatus(taskId, request));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<ApiResponse> deleteTask(
            @PathVariable Long taskId) {
        return ResponseEntity.ok(taskService.deleteTask(taskId));
    }
}