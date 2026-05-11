package com.campusconnect.campusconnect.controller;

import com.campusconnect.campusconnect.dto.response.ApiResponse;
import com.campusconnect.campusconnect.dto.response.TeamMemberResponse;
import com.campusconnect.campusconnect.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @PostMapping("/join/{projectId}")
    public ResponseEntity<ApiResponse> requestToJoin(
            @PathVariable Long projectId) {
        return ResponseEntity.ok(teamService.requestToJoin(projectId));
    }

    @GetMapping("/requests/{projectId}")
    public ResponseEntity<List<TeamMemberResponse>> getJoinRequests(
            @PathVariable Long projectId) {
        return ResponseEntity.ok(teamService.getJoinRequests(projectId));
    }

    @GetMapping("/members/{projectId}")
    public ResponseEntity<List<TeamMemberResponse>> getTeamMembers(
            @PathVariable Long projectId) {
        return ResponseEntity.ok(teamService.getTeamMembers(projectId));
    }

    @PutMapping("/respond/{memberId}")
    public ResponseEntity<ApiResponse> respondToRequest(
            @PathVariable Long memberId,
            @RequestParam String decision) {
        return ResponseEntity.ok(teamService.respondToRequest(memberId, decision));
    }

    @DeleteMapping("/leave/{projectId}")
    public ResponseEntity<ApiResponse> leaveProject(
            @PathVariable Long projectId) {
        return ResponseEntity.ok(teamService.leaveProject(projectId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<TeamMemberResponse>> getMyJoinedProjects() {
        return ResponseEntity.ok(teamService.getMyJoinedProjects());
    }
}