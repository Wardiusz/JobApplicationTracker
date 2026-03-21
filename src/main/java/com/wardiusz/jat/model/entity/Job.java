package com.wardiusz.jat.model.entity;

import com.wardiusz.jat.enums.JobContract;
import com.wardiusz.jat.enums.JobPosition;
import com.wardiusz.jat.enums.JobStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "jobs")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private String location;

    @Column(precision = 10, scale = 2)
    private BigDecimal salaryLowest;

    @Column(precision = 10, scale = 2)
    private BigDecimal salaryHighest;

    @DateTimeFormat
    @CreationTimestamp
    @Column(name = "createdAt", updatable = false)
    private LocalDate appliedDate;

    @DateTimeFormat
    @Column(name = "closingAt")
    private LocalDate closingDate;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobContract contract;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobPosition position;

    private String notes;

    @Column(name = "archived")
    private boolean isArchived;

    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
