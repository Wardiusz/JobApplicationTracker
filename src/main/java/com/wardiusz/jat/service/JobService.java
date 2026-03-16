package com.wardiusz.jat.service;

import com.wardiusz.jat.model.dto.JobDTO;
import com.wardiusz.jat.model.dto.JobFilter;
import com.wardiusz.jat.model.entity.Job;
import com.wardiusz.jat.model.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface JobService {

    List<JobDTO> getJobs(String username, JobFilter filter);

    JobDTO addJob(String username, JobDTO dto);

    JobDTO updateJob(String username, Long id, JobDTO dto);

    JobDTO updateNotes(String username, Long id, String notes);

    void archiveJob(String username, Long id);

    Job getOwnedJob(String username, Long id);

    User getUser(String username);

}