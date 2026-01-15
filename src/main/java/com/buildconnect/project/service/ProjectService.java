package com.buildconnect.project.service;

import com.buildconnect.org.model.Membership;
import com.buildconnect.org.model.OrgType;
import com.buildconnect.org.repository.MembershipRepository;
import com.buildconnect.org.repository.UserRepository;
import com.buildconnect.project.dto.ProjectDto;
import com.buildconnect.project.model.Project;
import com.buildconnect.project.model.ProjectStage;
import com.buildconnect.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;

    @Transactional
    public ProjectDto.ProjectResponse createProject(String userEmail, ProjectDto.CreateProjectRequest request) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // MVP: Get first membership
        var membership = membershipRepository.findByUser_Id(user.getId()).stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("User has no organization"));

        if (membership.getOrganization().getType() != OrgType.DEVELOPER) {
            throw new IllegalArgumentException("Only DEVELOPER organizations can create projects");
        }

        var project = Project.builder()
                .organization(membership.getOrganization())
                .title(request.getTitle())
                .city(request.getCity())
                .stage(request.getStage())
                .description(request.getDescription())
                .build();

        project = projectRepository.save(project);

        return mapToResponse(project);
    }

    @Transactional(readOnly = true)
    public Page<ProjectDto.ProjectResponse> listProjects(String city, ProjectStage stage, Pageable pageable) {
        return projectRepository.findAllByFilters(city, stage, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public ProjectDto.ProjectResponse getProject(UUID id) {
        return projectRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
    }

    private ProjectDto.ProjectResponse mapToResponse(Project project) {
        return ProjectDto.ProjectResponse.builder()
                .id(project.getId())
                .orgId(project.getOrganization().getId())
                .orgName(project.getOrganization().getName())
                .title(project.getTitle())
                .city(project.getCity())
                .stage(project.getStage())
                .description(project.getDescription())
                .createdAt(project.getCreatedAt())
                .build();
    }
}
