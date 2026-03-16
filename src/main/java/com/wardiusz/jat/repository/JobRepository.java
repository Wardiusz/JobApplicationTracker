package com.wardiusz.jat.repository;

import com.wardiusz.jat.model.entity.Job;
import com.wardiusz.jat.enums.JobPosition;
import com.wardiusz.jat.enums.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByUserIdAndArchivedFalse(Long userId);

    List<Job> findByUserId(Long userId);

    List<Job> findByUserIdAndArchivedTrue(Long userId);

    Long countByUserIdAndStatus(Long userId, JobStatus status);

    @Query("SELECT j FROM Job j WHERE j.user.id = :userId " +
            "AND (:archived = true OR j.isArchived = false) " +
            "AND (:status IS NULL OR j.status = :status) " +
            "AND (:position IS NULL OR j.position = :position) " +
            "AND (:company IS NULL OR " +
            "     LOWER(j.company) LIKE LOWER(:company))")
    List<Job> findWithFilters(
            @Param("userId") Long userId,
            @Param("archived") boolean archived,
            @Param("status") JobStatus status,
            @Param("position") JobPosition position,
            @Param("company") String company
    );
}
