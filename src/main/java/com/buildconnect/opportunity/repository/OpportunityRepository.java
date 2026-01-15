package com.buildconnect.opportunity.repository;

import com.buildconnect.opportunity.model.Opportunity;
import com.buildconnect.opportunity.model.OpportunityStatus;
import com.buildconnect.opportunity.model.OpportunityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OpportunityRepository extends JpaRepository<Opportunity, UUID> {

    @Query("SELECT o FROM Opportunity o WHERE " +
            "(:type IS NULL OR o.type = :type) AND " +
            "(:status IS NULL OR o.status = :status) AND " +
            "(:city IS NULL OR o.project.city = :city)")
    Page<Opportunity> findAllByFilters(
            @Param("type") OpportunityType type,
            @Param("status") OpportunityStatus status,
            @Param("city") String city,
            Pageable pageable);
}
