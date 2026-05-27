package com.wardiusz.jat.mapper;

import com.wardiusz.jat.dto.JobDTO;
import com.wardiusz.jat.entity.Job;
import com.wardiusz.jat.entity.User;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class JobMapper {

    public JobDTO toDto(Job job) {
        if (job == null) return null;
        return JobDTO.builder()
                .id(job.getId())
                .company(job.getCompany())
                .location(job.getLocation())
                .position(job.getPosition())
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
                .location(dto.getLocation())
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
        job.setLocation(dto.getLocation());
        job.setPosition(dto.getPosition());
        job.setSalaryLowest(dto.getSalaryLowest());
        job.setSalaryHighest(dto.getSalaryHighest());
        job.setClosingDate(dto.getDateClosing());
        job.setUrl(dto.getUrl());
        job.setStatus(dto.getStatus());
        job.setContract(dto.getContract());
        job.setArchived(dto.isArchived());
    }

    public List<JobDTO> toDTOList(List<Job> jobs) {
        return jobs.stream()
                .map(JobMapper::toDto)
                .toList();
    }

}
