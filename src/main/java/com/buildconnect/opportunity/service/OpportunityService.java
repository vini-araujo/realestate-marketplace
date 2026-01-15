package com.buildconnect.opportunity.service;

import com.buildconnect.opportunity.dto.OpportunityDto;
import com.buildconnect.opportunity.model.Opportunity;
import com.buildconnect.opportunity.model.OpportunityStatus;
import com.buildconnect.opportunity.model.OpportunityType;
import com.buildconnect.opportunity.repository.OpportunityRepository;
import com.buildconnect.org.model.OrgType;
import com.buildconnect.org.repository.MembershipRepository;
import com.buildconnect.org.repository.UserRepository;
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
public class OpportunityService {

        private final OpportunityRepository opportunityRepository;
        private final ProjectRepository projectRepository;
        private final UserRepository userRepository;
        private final MembershipRepository membershipRepository;

        @Transactional
        public OpportunityDto.OpportunityResponse createOpportunity(String userEmail, UUID projectId,
                        OpportunityDto.CreateOpportunityRequest request) {
                var user = userRepository.findByEmail(userEmail)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
                var membership = membershipRepository.findByUser_Id(user.getId()).stream().findFirst()
                                .orElseThrow(() -> new IllegalStateException("User has no organization"));

                var project = projectRepository.findById(projectId)
                                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

                if (!project.getOrganization().getId().equals(membership.getOrganization().getId())) {
                        throw new IllegalArgumentException(
                                        "Only the owning organization can create opportunities for this project");
                }

                var opportunity = Opportunity.builder()
                                .project(project)
                                .type(request.getType())
                                .status(OpportunityStatus.OPEN)
                                .deadline(request.getDeadline())
                                .requirements(request.getRequirements())
                                .build();

                opportunity = opportunityRepository.save(opportunity);

                return mapToResponse(opportunity);
        }

        @Transactional(readOnly = true)
        public Page<OpportunityDto.OpportunityResponse> listOpportunities(OpportunityType type,
                        OpportunityStatus status,
                        String city, Pageable pageable) {
                return opportunityRepository.findAllByFilters(type, status, city, pageable)
                                .map(this::mapToResponse);
        }

        @Transactional
        public void closeOpportunity(String userEmail, UUID id) {
                var user = userRepository.findByEmail(userEmail)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
                var membership = membershipRepository.findByUser_Id(user.getId()).stream().findFirst()
                                .orElseThrow(() -> new IllegalStateException("User has no organization"));

                var opportunity = opportunityRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Opportunity not found"));

                if (!opportunity.getProject().getOrganization().getId().equals(membership.getOrganization().getId())) {
                        throw new IllegalArgumentException("Only the owning organization can close this opportunity");
                }

                opportunity.setStatus(OpportunityStatus.CLOSED);
                opportunityRepository.save(opportunity);
        }

        private OpportunityDto.OpportunityResponse mapToResponse(Opportunity opportunity) {
                return OpportunityDto.OpportunityResponse.builder()
                                .id(opportunity.getId())
                                .projectId(opportunity.getProject().getId())
                                .projectTitle(opportunity.getProject().getTitle())
                                .projectCity(opportunity.getProject().getCity())
                                .type(opportunity.getType())
                                .status(opportunity.getStatus())
                                .deadline(opportunity.getDeadline())
                                .requirements(opportunity.getRequirements())
                                .createdAt(opportunity.getCreatedAt())
                                .build();
        }
}
