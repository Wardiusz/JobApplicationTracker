package com.wardiusz.jat.dto;

import com.wardiusz.jat.enums.JobContract;
import com.wardiusz.jat.enums.JobPosition;
import com.wardiusz.jat.enums.JobStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobDTO {

    private Long id;

    @NotBlank
    private String company;

    @NotBlank
    private String location;

    @Positive
    private BigDecimal salaryLowest;

    @Positive
    private BigDecimal salaryHighest;

    @NotNull
    private LocalDate dateApplied;

    private LocalDate dateClosing;

    @NotBlank
    private String url;

    private String notes;

    private boolean archived;

    @NotNull
    private JobStatus status;

    private JobPosition position;

    private JobContract contract;
}
