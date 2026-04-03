package com.wardiusz.jat.service;

import com.wardiusz.jat.dto.JobDTO;
import com.wardiusz.jat.dto.JobFilter;
import com.wardiusz.jat.entity.Job;
import com.wardiusz.jat.entity.User;

import java.util.List;


public interface JobService {

    List<JobDTO> getJobs(String username, JobFilter filter);

    JobDTO addJob(String username, JobDTO dto);

    JobDTO updateJob(String username, Long id, JobDTO dto);

    JobDTO updateNotes(String username, Long id, String notes);

    void archiveJob(String username, Long id);

    void unArchiveJob(String username, Long id);

    Job getOwnedJob(String username, Long id);

    User getUser(String username);

}