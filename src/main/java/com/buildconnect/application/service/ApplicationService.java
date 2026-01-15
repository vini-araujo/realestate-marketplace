package com.buildconnect.application.service;

import com.buildconnect.application.dto.ApplicationDto;
import com.buildconnect.application.model.Application;
import com.buildconnect.application.model.ApplicationStatus;
import com.buildconnect.application.repository.ApplicationRepository;
import com.buildconnect.opportunity.model.OpportunityStatus;
import com.buildconnect.opportunity.repository.OpportunityRepository;
import com.buildconnect.org.model.OrgType;
import com.buildconnect.org.model.Role;
import com.buildconnect.org.repository.MembershipRepository;
import com.buildconnect.org.repository.UserRepository;
import com.buildconnect.partnership.model.Partnership;
import com.buildconnect.partnership.repository.PartnershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationService {

        private final ApplicationRepository applicationRepository;
        private final OpportunityRepository opportunityRepository;
        private final UserRepository userRepository;
        private final MembershipRepository membershipRepository;
        private final PartnershipRepository partnershipRepository;

        @Transactional
        public ApplicationDto.ApplicationResponse apply(String userEmail, UUID opportunityId,
                        ApplicationDto.CreateApplicationRequest request) {
                var user = userRepository.findByEmail(userEmail)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
                var membership = membershipRepository.findByUser_Id(user.getId()).stream().findFirst()
                                .orElseThrow(() -> new IllegalStateException("User has no organization"));

                if (membership.getOrganization().getType() == OrgType.DEVELOPER) {
                        throw new IllegalArgumentException("Developers cannot apply to opportunities");
                }

                var opportunity = opportunityRepository.findById(opportunityId)
                                .orElseThrow(() -> new IllegalArgumentException("Opportunity not found"));

                if (opportunity.getStatus() != OpportunityStatus.OPEN) {
                        throw new IllegalArgumentException("Opportunity is not open");
                }

                if (applicationRepository.existsByOpportunityIdAndApplicantOrgId(opportunityId,
                                membership.getOrganization().getId())) {
                        throw new IllegalArgumentException("Organization has already applied to this opportunity");
                }

                var application = Application.builder()
                                .opportunity(opportunity)
                                .applicantOrg(membership.getOrganization())
                                .status(ApplicationStatus.APPLIED)
                                .proposal(request.getProposal())
                                .price(request.getPrice())
                                .timelineDays(request.getTimelineDays())
                                .build();

                application = applicationRepository.save(application);

                return mapToResponse(application);
        }

        @Transactional(readOnly = true)
        public Page<ApplicationDto.ApplicationResponse> listApplications(String userEmail, boolean myOrg,
                        ApplicationStatus status, Pageable pageable) {
                var user = userRepository.findByEmail(userEmail)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
                var membership = membershipRepository.findByUser_Id(user.getId()).stream().findFirst()
                                .orElseThrow(() -> new IllegalStateException("User has no organization"));

                if (myOrg) {
                        return applicationRepository
                                        .findAllByApplicantOrgIdAndStatus(membership.getOrganization().getId(), status,
                                                        pageable)
                                        .map(this::mapToResponse);
                } else {
                        if (membership.getOrganization().getType() != OrgType.DEVELOPER) {
                                throw new IllegalArgumentException(
                                                "Only developers can view applications for their projects");
                        }
                        return applicationRepository
                                        .findAllByDeveloperOrgIdAndStatus(membership.getOrganization().getId(), status,
                                                        pageable)
                                        .map(this::mapToResponse);
                }
        }

        @Transactional
        public void approveApplication(String userEmail, UUID applicationId) {
                var user = userRepository.findByEmail(userEmail)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
                var membership = membershipRepository.findByUser_Id(user.getId()).stream().findFirst()
                                .orElseThrow(() -> new IllegalStateException("User has no organization"));

                if (membership.getRole() != Role.ORG_ADMIN) {
                        throw new IllegalArgumentException("Only ORG_ADMIN can approve applications");
                }

                var application = applicationRepository.findById(applicationId)
                                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

                if (!application.getOpportunity().getProject().getOrganization().getId()
                                .equals(membership.getOrganization().getId())) {
                        throw new IllegalArgumentException("Only the owning developer can approve applications");
                }

                if (application.getStatus() == ApplicationStatus.REJECTED) {
                        throw new IllegalArgumentException("Cannot approve a rejected application");
                }

                application.setStatus(ApplicationStatus.APPROVED);
                applicationRepository.save(application);

                var partnership = Partnership.builder()
                                .project(application.getOpportunity().getProject())
                                .partnerOrg(application.getApplicantOrg())
                                .type(application.getOpportunity().getType())
                                .build();
                partnershipRepository.save(partnership);
        }

        @Transactional
        public void rejectApplication(String userEmail, UUID applicationId) {
                var user = userRepository.findByEmail(userEmail)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
                var membership = membershipRepository.findByUser_Id(user.getId()).stream().findFirst()
                                .orElseThrow(() -> new IllegalStateException("User has no organization"));

                if (membership.getRole() != Role.ORG_ADMIN) {
                        throw new IllegalArgumentException("Only ORG_ADMIN can reject applications");
                }

                var application = applicationRepository.findById(applicationId)
                                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

                if (!application.getOpportunity().getProject().getOrganization().getId()
                                .equals(membership.getOrganization().getId())) {
                        throw new IllegalArgumentException("Only the owning developer can reject applications");
                }

                if (application.getStatus() == ApplicationStatus.APPROVED) {
                        throw new IllegalArgumentException("Cannot reject an approved application");
                }

                application.setStatus(ApplicationStatus.REJECTED);
                applicationRepository.save(application);
        }

        private ApplicationDto.ApplicationResponse mapToResponse(Application application) {
                return ApplicationDto.ApplicationResponse.builder()
                                .id(application.getId())
                                .opportunityId(application.getOpportunity().getId())
                                .applicantOrgId(application.getApplicantOrg().getId())
                                .applicantOrgName(application.getApplicantOrg().getName())
                                .status(application.getStatus())
                                .proposal(application.getProposal())
                                .price(application.getPrice())
                                .timelineDays(application.getTimelineDays())
                                .createdAt(application.getCreatedAt())
                                .build();
        }
}
