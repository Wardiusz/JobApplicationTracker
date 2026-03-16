package com.wardiusz.jat.mapper;

import com.wardiusz.jat.model.dto.JobDTO;
import com.wardiusz.jat.model.entity.Job;
import com.wardiusz.jat.model.entity.User;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class JobMapper {

    public JobDTO toDTO(Job job) {
        if (job == null) return null;
        return JobDTO.builder()
                .company(job.getCompany())
                .jobPosition(job.getPosition())
                .salaryLowest(job.getSalaryLowest())
                .salaryHighest(job.getSalaryHighest())
                .dateApplied(job.getAppliedDate())
                .dateClosing(job.getClosingDate())
                .url(job.getUrl())
                .notes(job.getNotes())
                .archived(job.isArchived())
                .status(job.getStatus())
                .position(job.getPosition())
                .contract(job.getContract())
                .build();
    }

    public Job toEntity(JobDTO dto, User user) {
        if (dto == null) return null;
        return Job.builder()
                .company(dto.getCompany())
                .position(dto.getPosition())
                .salaryLowest(dto.getSalaryLowest())
                .salaryHighest(dto.getSalaryHighest())
                .appliedDate(dto.getDateApplied())
                .closingDate(dto.getDateClosing())
                .url(dto.getUrl())
                .notes(dto.getNotes())
                .status(dto.getStatus())
                .position(dto.getPosition())
                .contract(dto.getContract())
                .user(user)
                .build();
    }

    public void updateEntity(Job job, JobDTO dto) {
        if (job == null || dto == null) return;
        job.setCompany(dto.getCompany());
        job.setPosition(dto.getPosition());
        job.setSalaryLowest(dto.getSalaryLowest());
        job.setSalaryHighest(dto.getSalaryHighest());
        job.setClosingDate(dto.getDateClosing());
        job.setUrl(dto.getUrl());
        job.setStatus(dto.getStatus());
        job.setPosition(dto.getPosition());
        job.setContract(dto.getContract());
    }

    public List<JobDTO> toDTOList(List<Job> jobs) {
        return jobs.stream()
                .map(JobMapper::toDTO)
                .toList();
    }
}
