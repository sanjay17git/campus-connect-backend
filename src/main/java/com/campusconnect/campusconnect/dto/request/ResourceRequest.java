package com.campusconnect.campusconnect.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResourceRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "URL is required")
    private String url;

    @NotNull(message = "Type is required")
    private String type;

    @NotNull(message = "Project ID is required")
    private Long projectId;
}