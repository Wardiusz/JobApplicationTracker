package com.wardiusz.jat.dto;

import com.wardiusz.jat.enums.JobContract;
import com.wardiusz.jat.enums.JobPosition;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CreateJobRequest {

    @NotBlank(message = "")
    private String company;

    @NotBlank(message = "")
    private String location;

    @Nullable
    @Positive
    private BigDecimal salaryLowest;

    @Positive
    private BigDecimal salaryHighest;

    @NotBlank(message = "")
    private String url;

    @NotBlank(message = "")
    private LocalDateTime closingAt;

    @NotBlank(message = "")
    private JobContract contract;

    @NotBlank(message = "")
    private JobPosition position;

    @Nullable
    private String notes;
}
