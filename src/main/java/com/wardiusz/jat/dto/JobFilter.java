package com.wardiusz.jat.dto;

import com.wardiusz.jat.enums.JobContract;
import com.wardiusz.jat.enums.JobPosition;
import com.wardiusz.jat.enums.JobStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobFilter {

    private JobStatus status;
    private JobPosition position;
    private JobContract contract;
    private String companyName;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private boolean includeArchived;
}
