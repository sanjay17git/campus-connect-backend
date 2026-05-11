package com.campusconnect.campusconnect.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamMemberResponse {

    private Long id;
    private String userName;
    private String userEmail;
    private String status;
    private LocalDateTime requestedAt;
    private LocalDateTime respondedAt;
}