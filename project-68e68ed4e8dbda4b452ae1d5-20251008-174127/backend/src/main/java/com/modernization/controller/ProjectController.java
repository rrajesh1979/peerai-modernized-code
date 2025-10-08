package com.modernization.controller;

import com.modernization.dto.ProjectDTO;
import com.modernization.dto.TaskDTO;
import com.modernization.model.Project;
import com.modernization.service.ProjectService;
import com.modernization.service.TaskService;
import com.modernization.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * REST controller for managing Projects.
 * Provides endpoints for creating, retrieving, updating, and deleting projects,
 * as well as managing project team members and associated tasks.
 */
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final ProjectService projectService;
    private final TaskService taskService;

    /**
     * Creates a new project.
     *
     * @param projectDTO the project to create
     * @return the created project with status 201 (Created)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    public ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody ProjectDTO projectDTO) {
        log.debug("REST request to create Project : {}", projectDTO);
        if (projectDTO.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new project cannot already have an ID");
        }
        
        // Set current user as owner if not specified
        if (projectDTO.getOwnerId() == null) {
            projectDTO.setOwnerId(SecurityUtils.getCurrentUserId());
        }
        
        ProjectDTO result = projectService.save(projectDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Updates an existing project.
     *
     * @param projectDTO the project to update
     * @return the updated project
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    public ResponseEntity<ProjectDTO> updateProject(
            @PathVariable String id,
            @Valid @RequestBody ProjectDTO projectDTO) {
        log.debug("REST request to update Project : {}", projectDTO);
        
        if (projectDTO.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Project ID must be provided");
        }
        
        if (!id.equals(projectDTO.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Path ID doesn't match request body ID");
        }
        
        // Verify user has permission to update this project
        projectService.checkProjectAccess(id);
        
        ProjectDTO result = projectService.update(projectDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * Gets a project by ID.
     *
     * @param id the ID of the project to retrieve
     * @return the project
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getProject(@PathVariable String id) {
        log.debug("REST request to get Project : {}", id);
        
        // Verify user has permission to view this project
        projectService.checkProjectAccess(id);
        
        return projectService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found with id: " + id));
    }

    /**
     * Gets all projects with pagination.
     *
     * @param pageable pagination information
     * @return the list of projects
     */
    @GetMapping
    public ResponseEntity<Page<ProjectDTO>> getAllProjects(Pageable pageable) {
        log.debug("REST request to get all Projects");
        Page<ProjectDTO> page = projectService.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * Gets all projects for the current user.
     *
     * @return the list of projects
     */
    @GetMapping("/my-projects")
    public ResponseEntity<List<ProjectDTO>> getMyProjects() {
        log.debug("REST request to get current user's Projects");
        String userId = SecurityUtils.getCurrentUserId();
        List<ProjectDTO> projects = projectService.findByOwnerId(userId);
        return ResponseEntity.ok(projects);
    }

    /**
     * Gets all projects where the current user is a team member.
     *
     * @return the list of projects
     */
    @GetMapping("/my-team-projects")
    public ResponseEntity<List<ProjectDTO>> getMyTeamProjects() {
        log.debug("REST request to get Projects where current user is a team member");
        String userId = SecurityUtils.getCurrentUserId();
        List<ProjectDTO> projects = projectService.findByTeamMemberId(userId);
        return ResponseEntity.ok(projects);
    }

    /**
     * Deletes a project.
     *
     * @param id the ID of the project to delete
     * @return no content with status 204 (No Content)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    public ResponseEntity<Void> deleteProject(@PathVariable String id) {
        log.debug("REST request to delete Project : {}", id);
        
        // Verify user has permission to delete this project
        projectService.checkProjectOwnership(id);
        
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Adds a team member to a project.
     *
     * @param id the ID of the project
     * @param userId the ID of the user to add
     * @param role the role of the user in the project
     * @return the updated project
     */
    @PostMapping("/{id}/team-members")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    public ResponseEntity<ProjectDTO> addTeamMember(
            @PathVariable String id,
            @RequestParam String userId,
            @RequestParam(defaultValue = "MEMBER") String role) {
        log.debug("REST request to add team member {} with role {} to Project : {}", userId, role, id);
        
        // Verify user has permission to modify this project
        projectService.checkProjectOwnership(id);
        
        ProjectDTO result = projectService.addTeamMember(id, userId, role);
        return ResponseEntity.ok(result);
    }

    /**
     * Removes a team member from a project.
     *
     * @param id the ID of the project
     * @param userId the ID of the user to remove
     * @return the updated project
     */
    @DeleteMapping("/{id}/team-members/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    public ResponseEntity<ProjectDTO> removeTeamMember(
            @PathVariable String id,
            @PathVariable String userId) {
        log.debug("REST request to remove team member {} from Project : {}", userId, id);
        
        // Verify user has permission to modify this project
        projectService.checkProjectOwnership(id);
        
        ProjectDTO result = projectService.removeTeamMember(id, userId);
        return ResponseEntity.ok(result);
    }

    /**
     * Gets all tasks for a project.
     *
     * @param id the ID of the project
     * @return the list of tasks
     */
    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<TaskDTO>> getProjectTasks(@PathVariable String id) {
        log.debug("REST request to get all Tasks for Project : {}", id);
        
        // Verify user has permission to view this project
        projectService.checkProjectAccess(id);
        
        List<TaskDTO> tasks = taskService.findByProjectId(id);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Updates the status of a project.
     *
     * @param id the ID of the project
     * @param status the new status
     * @return the updated project
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    public ResponseEntity<ProjectDTO> updateProjectStatus(
            @PathVariable String id,
            @RequestParam String status) {
        log.debug("REST request to update status of Project {} to {}", id, status);
        
        // Verify user has permission to modify this project
        projectService.checkProjectAccess(id);
        
        ProjectDTO result = projectService.updateStatus(id, status);
        return ResponseEntity.ok(result);
    }

    /**
     * Gets project statistics.
     *
     * @param id the ID of the project
     * @return the project statistics
     */
    @GetMapping("/{id}/statistics")
    public ResponseEntity<Object> getProjectStatistics(@PathVariable String id) {
        log.debug("REST request to get statistics for Project : {}", id);
        
        // Verify user has permission to view this project
        projectService.checkProjectAccess(id);
        
        Object statistics = projectService.getProjectStatistics(id);
        return ResponseEntity.ok(statistics);
    }
}