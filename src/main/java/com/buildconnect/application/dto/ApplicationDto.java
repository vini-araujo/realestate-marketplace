package com.buildconnect.application.dto;

import com.buildconnect.application.model.ApplicationStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class ApplicationDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateApplicationRequest {
        @NotBlank
        private String proposal;
        private BigDecimal price;
        private Integer timelineDays;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApplicationResponse {
        private UUID id;
        private UUID opportunityId;
        private UUID applicantOrgId;
        private String applicantOrgName;
        private ApplicationStatus status;
        private String proposal;
        private BigDecimal price;
        private Integer timelineDays;
        private Instant createdAt;
    }
}
