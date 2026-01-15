package com.buildconnect.application.controller;

import com.buildconnect.application.dto.ApplicationDto;
import com.buildconnect.application.model.ApplicationStatus;
import com.buildconnect.application.service.ApplicationService;
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
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/opportunities/{id}/applications")
    public ResponseEntity<ApplicationDto.ApplicationResponse> apply(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID id,
            @RequestBody @Valid ApplicationDto.CreateApplicationRequest request) {
        return ResponseEntity.ok(applicationService.apply(userDetails.getUsername(), id, request));
    }

    @GetMapping("/applications")
    public ResponseEntity<Page<ApplicationDto.ApplicationResponse>> listApplications(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "true") boolean myOrg,
            @RequestParam(required = false) ApplicationStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity
                .ok(applicationService.listApplications(userDetails.getUsername(), myOrg, status, pageable));
    }

    @PostMapping("/applications/{id}/approve")
    public ResponseEntity<Void> approveApplication(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID id) {
        applicationService.approveApplication(userDetails.getUsername(), id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/applications/{id}/reject")
    public ResponseEntity<Void> rejectApplication(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID id) {
        applicationService.rejectApplication(userDetails.getUsername(), id);
        return ResponseEntity.ok().build();
    }
}
