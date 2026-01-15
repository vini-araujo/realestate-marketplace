package com.buildconnect.opportunity.controller;

import com.buildconnect.opportunity.dto.OpportunityDto;
import com.buildconnect.opportunity.model.OpportunityStatus;
import com.buildconnect.opportunity.model.OpportunityType;
import com.buildconnect.opportunity.service.OpportunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OpportunityController {

    private final OpportunityService opportunityService;

    @PostMapping("/projects/{projectId}/opportunities")
    public ResponseEntity<OpportunityDto.OpportunityResponse> createOpportunity(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID projectId,
            @RequestBody @Valid OpportunityDto.CreateOpportunityRequest request) {
        return ResponseEntity.ok(opportunityService.createOpportunity(userDetails.getUsername(), projectId, request));
    }

    @GetMapping("/opportunities")
    public ResponseEntity<Page<OpportunityDto.OpportunityResponse>> listOpportunities(
            @RequestParam(required = false) OpportunityType type,
            @RequestParam(required = false) OpportunityStatus status,
            @RequestParam(required = false) String city,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(opportunityService.listOpportunities(type, status, city, pageable));
    }

    @PostMapping("/opportunities/{id}/close")
    public ResponseEntity<Void> closeOpportunity(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID id) {
        opportunityService.closeOpportunity(userDetails.getUsername(), id);
        return ResponseEntity.ok().build();
    }
}
