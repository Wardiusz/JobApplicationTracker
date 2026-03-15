package com.wardiusz.jat.model.dto;

import com.wardiusz.jat.enums.JobContract;
import com.wardiusz.jat.enums.JobPosition;
import com.wardiusz.jat.enums.JobStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    private JobPosition jobPosition;

    private BigDecimal salary;

    @NotNull
    private LocalDate dateApplied;

    private LocalDate dateClosing;

    private String jobUrl;

    private String notes;

    private boolean archived;

    @NotNull
    private JobStatus status;

    private JobPosition position;

    private JobContract contract;
}
