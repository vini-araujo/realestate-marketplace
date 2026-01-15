package com.buildconnect.project.repository;

import com.buildconnect.project.model.Project;
import com.buildconnect.project.model.ProjectStage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    @Query("SELECT p FROM Project p WHERE " +
            "(:city IS NULL OR p.city = :city) AND " +
            "(:stage IS NULL OR p.stage = :stage)")
    Page<Project> findAllByFilters(
            @Param("city") String city,
            @Param("stage") ProjectStage stage,
            Pageable pageable);
}
