package com.wardiusz.jat.service;

import com.wardiusz.jat.dto.JobDTO;
import com.wardiusz.jat.dto.JobFilter;
import com.wardiusz.jat.model.entity.Job;
import com.wardiusz.jat.model.entity.User;
import com.wardiusz.jat.repository.JobRepository;
import com.wardiusz.jat.repository.UserRepository;
import com.wardiusz.jat.mapper.JobMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class JobServiceImpl implements JobService {

    private final JobRepository  jobRepository;
    private final UserRepository  userRepository;

    public List<JobDTO> getJobs(String username, JobFilter filter) {
        User user = getUser(username);

        return JobMapper.toDTOList(
                jobRepository.findWithFilters(
                        user.getId(),
                        filter.isIncludeArchived(),
                        filter.getStatus(),
                        filter.getPosition(),
                        filter.getCompanyName()
                )
        );
    }

    public JobDTO addJob(String username, JobDTO dto) {
        User user = getUser(username);
        Job job = JobMapper.toEntity(dto, user);
        return JobMapper.toDTO(
                jobRepository.save(job));
    }

    public JobDTO updateJob(String username, Long id, JobDTO dto) {
        Job job = getOwnedJob(username, id);
        JobMapper.updateEntity(job, dto);

        return JobMapper.toDTO(jobRepository.save(job));
    }

    public JobDTO updateNotes(String username, Long id, String notes) {
        Job job = getOwnedJob(username, id);
        job.setNotes(notes);

        return JobMapper.toDTO(jobRepository.save(job));
    }

    public void archiveJob(String username, Long id) {
        Job job = getOwnedJob(username, id);
        job.setArchived(true);

        jobRepository.save(job);
    }

    public Job getOwnedJob(String username, Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new
                        ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!job.getUser().getUsername().equals(username))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        return job;
    }

    public User getUser(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new
                        ResponseStatusException(
                        HttpStatus.NOT_FOUND));
    }
}
