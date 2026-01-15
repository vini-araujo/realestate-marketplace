package com.buildconnect.org.repository;

import com.buildconnect.org.model.Membership;
import com.buildconnect.org.model.MembershipId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, MembershipId> {
    List<Membership> findByUser_Id(UUID userId);

    Optional<Membership> findByUser_IdAndOrganization_Id(UUID userId, UUID orgId);
}
