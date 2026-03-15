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
                .id(job.getId())
                .company(job.getCompany())
                .jobPosition(job.getPosition())
                .salary(job.getSalary())
                .dateApplied(job.getAppliedDate())
                .dateClosing(job.getClosingDate())
                .jobUrl(job.getUrl())
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
                .salary(dto.getSalary())
                .appliedDate(dto.getDateApplied())
                .closingDate(dto.getDateClosing())
                .url(dto.getJobUrl())
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
        job.setSalary(dto.getSalary());
        job.setClosingDate(dto.getDateClosing());
        job.setUrl(dto.getJobUrl());
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
