package com.campusconnect.campusconnect.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskStatusRequest {

    @NotNull(message = "Status is required")
    private String status;
}