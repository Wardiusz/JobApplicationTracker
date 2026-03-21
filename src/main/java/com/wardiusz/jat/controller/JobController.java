package com.wardiusz.jat.controller;

import com.wardiusz.jat.model.dto.JobDTO;
import com.wardiusz.jat.model.dto.JobFilter;
import com.wardiusz.jat.service.JobService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {

    private final JobService jobService;

    // GET /api/jobs?status=&position=&includeArchived=
    @GetMapping
    public ResponseEntity<List<JobDTO>> getJobs(Authentication auth, @ModelAttribute JobFilter filter) {
        return ResponseEntity.ok(jobService.getJobs(auth.getName(), filter));
    }

    // POST /api/jobs
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<JobDTO> addJob(Authentication auth, @RequestBody @Valid JobDTO dto) {
        return ResponseEntity.ok(jobService.addJob(auth.getName(), dto));
    }

    // PUT /api/jobs/{id}
    @PutMapping("/{id}")
    public ResponseEntity<JobDTO> updateJob(Authentication auth, @PathVariable Long id, @RequestBody @Valid JobDTO dto) {
        return ResponseEntity.ok(jobService.updateJob(auth.getName(), id, dto));
    }

    // PATCH /api/jobs/{id}/notes
    @PatchMapping("/{id}/notes")
    public JobDTO updateNotes(Authentication auth, @PathVariable Long id, @RequestBody String notes) {
        return jobService.updateNotes(auth.getName(), id, notes);
    }

    // PATCH /api/jobs/{id}/archive
    @PatchMapping("/{id}/archive")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void archiveJob(Authentication auth, @PathVariable Long id) {
        jobService.archiveJob(auth.getName(), id);
    }
}