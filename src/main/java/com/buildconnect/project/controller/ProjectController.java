package com.buildconnect.project.controller;

import com.buildconnect.project.dto.ProjectDto;
import com.buildconnect.project.model.ProjectStage;
import com.buildconnect.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectDto.ProjectResponse> createProject(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid ProjectDto.CreateProjectRequest request) {
        return ResponseEntity.ok(projectService.createProject(userDetails.getUsername(), request));
    }

    @GetMapping
    public ResponseEntity<Page<ProjectDto.ProjectResponse>> listProjects(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) ProjectStage stage,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(projectService.listProjects(city, stage, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto.ProjectResponse> getProject(@PathVariable UUID id) {
        return ResponseEntity.ok(projectService.getProject(id));
    }
}
