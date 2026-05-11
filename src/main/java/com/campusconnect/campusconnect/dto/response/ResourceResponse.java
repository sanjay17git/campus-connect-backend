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
public class ResourceResponse {

    private Long id;
    private String title;
    private String url;
    private String type;
    private String uploadedByName;
    private Long projectId;
    private LocalDateTime uploadedAt;
}