package com.buildconnect.application.repository;

import com.buildconnect.application.model.Application;
import com.buildconnect.application.model.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {

    boolean existsByOpportunityIdAndApplicantOrgId(UUID opportunityId, UUID applicantOrgId);

    @Query("SELECT a FROM Application a WHERE " +
            "(:applicantOrgId IS NULL OR a.applicantOrg.id = :applicantOrgId) AND " +
            "(:status IS NULL OR a.status = :status)")
    Page<Application> findAllByApplicantOrgIdAndStatus(
            @Param("applicantOrgId") UUID applicantOrgId,
            @Param("status") ApplicationStatus status,
            Pageable pageable);

    @Query("SELECT a FROM Application a WHERE " +
            "a.opportunity.project.organization.id = :developerOrgId AND " +
            "(:status IS NULL OR a.status = :status)")
    Page<Application> findAllByDeveloperOrgIdAndStatus(
            @Param("developerOrgId") UUID developerOrgId,
            @Param("status") ApplicationStatus status,
            Pageable pageable);
}
