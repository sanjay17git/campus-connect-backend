package com.campusconnect.campusconnect.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponse {

    private Long id;
    private String title;
    private String description;
    private String status;
    private List<String> tags;
    private int likesCount;
    private String ownerName;
    private String ownerEmail;
    private LocalDateTime createdAt;
    private int maxTeamSize;
}