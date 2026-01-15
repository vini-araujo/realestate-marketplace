package com.buildconnect.project.dto;

import com.buildconnect.project.model.ProjectStage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

public class ProjectDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateProjectRequest {
        @NotBlank
        private String title;
        @NotBlank
        private String city;
        @NotNull
        private ProjectStage stage;
        private String description;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectResponse {
        private UUID id;
        private UUID orgId;
        private String orgName;
        private String title;
        private String city;
        private ProjectStage stage;
        private String description;
        private Instant createdAt;
    }
}
