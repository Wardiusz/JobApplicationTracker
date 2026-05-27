package com.wardiusz.jat.dto.request;

import com.wardiusz.jat.enums.JobContract;
import com.wardiusz.jat.enums.JobPosition;
import com.wardiusz.jat.enums.JobStatus;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CreateJobRequest {

    private Long id;

    @NotBlank(message = "")
    private String company;

    @NotBlank(message = "")
    private String location;

    @Positive
    private BigDecimal salaryLowest;

    @Positive
    private BigDecimal salaryHighest;

    @NotBlank(message = "")
    private String url;

    @NotNull
    private LocalDateTime dateApplied;

    private LocalDateTime dateClosing;

    @NotNull
    private JobStatus status;

    @NotBlank(message = "")
    private JobContract contract;

    @NotBlank(message = "")
    private JobPosition position;

    @Nullable
    private String notes;

    private boolean archived;
}