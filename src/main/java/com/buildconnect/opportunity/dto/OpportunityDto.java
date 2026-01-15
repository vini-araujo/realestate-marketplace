package com.buildconnect.opportunity.dto;

import com.buildconnect.opportunity.model.OpportunityStatus;
import com.buildconnect.opportunity.model.OpportunityType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

public class OpportunityDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateOpportunityRequest {
        @NotNull
        private OpportunityType type;
        private Instant deadline;
        private String requirements;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OpportunityResponse {
        private UUID id;
        private UUID projectId;
        private String projectTitle;
        private String projectCity;
        private OpportunityType type;
        private OpportunityStatus status;
        private Instant deadline;
        private String requirements;
        private Instant createdAt;
    }
}
