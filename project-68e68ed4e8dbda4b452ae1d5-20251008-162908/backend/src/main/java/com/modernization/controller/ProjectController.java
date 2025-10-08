package com.modernization.controller;

import com.modernization.dto.ProjectDTO;
import com.modernization.dto.TaskDTO;
import com.modernization.model.Project;
import com.modernization.service.ProjectService;
import com.modernization.service.TaskService;
import com.modernization.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing Project resources.
 * Provides endpoints for CRUD operations and project-related functionalities.
 */
@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final ProjectService projectService;
    private final TaskService taskService;

    /**
     * Creates a new project.
     *
     * @param projectDTO the project to create
     * @return the created project with HTTP status 201
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    public ResponseEntity<ApiResponse<ProjectDTO>> createProject(@Valid @RequestBody ProjectDTO projectDTO) {
        log.info("REST request to create Project: {}", projectDTO.getName());
        ProjectDTO result = projectService.createProject(projectDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Project created successfully", result));
    }

    /**
     * Updates an existing project.
     *
     * @param id the ID of the project to update
     * @param projectDTO the project details to update
     * @return the updated project
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    public ResponseEntity<ApiResponse<ProjectDTO>> updateProject(
            @PathVariable String id,
            @Valid @RequestBody ProjectDTO projectDTO) {
        log.info("REST request to update Project: {}", id);
        ProjectDTO result = projectService.updateProject(id, projectDTO);
        return ResponseEntity.ok(new ApiResponse<>(true, "Project updated successfully", result));
    }

    /**
     * Retrieves a project by its ID.
     *
     * @param id the ID of the project to retrieve
     * @return the project
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER', 'USER')")
    public ResponseEntity<ApiResponse<ProjectDTO>> getProject(@PathVariable String id) {
        log.info("REST request to get Project: {}", id);
        ProjectDTO projectDTO = projectService.getProjectById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Project retrieved successfully", projectDTO));
    }

    /**
     * Retrieves all projects with pagination.
     *
     * @param pageable pagination information
     * @return the list of projects
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER', 'USER')")
    public ResponseEntity<ApiResponse<Page<ProjectDTO>>> getAllProjects(Pageable pageable) {
        log.info("REST request to get all Projects");
        Page<ProjectDTO> page = projectService.getAllProjects(pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Projects retrieved successfully", page));
    }

    /**
     * Deletes a project by its ID.
     *
     * @param id the ID of the project to delete
     * @return no content with HTTP status 204
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable String id) {
        log.info("REST request to delete Project: {}", id);
        projectService.deleteProject(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(new ApiResponse<>(true, "Project deleted successfully", null));
    }

    /**
     * Retrieves all projects for the current user.
     *
     * @return the list of projects
     */
    @GetMapping("/my-projects")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<ProjectDTO>>> getMyProjects() {
        log.info("REST request to get all Projects for current user");
        List<ProjectDTO> projects = projectService.getProjectsForCurrentUser();
        return ResponseEntity.ok(new ApiResponse<>(true, "User projects retrieved successfully", projects));
    }

    /**
     * Adds a team member to a project.
     *
     * @param projectId the ID of the project
     * @param userId the ID of the user to add
     * @param role the role of the user in the project
     * @return the updated project
     */
    @PostMapping("/{projectId}/team-members/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    public ResponseEntity<ApiResponse<ProjectDTO>> addTeamMember(
            @PathVariable String projectId,
            @PathVariable String userId,
            @RequestParam String role) {
        log.info("REST request to add team member {} to Project: {}", userId, projectId);
        ProjectDTO result = projectService.addTeamMember(projectId, userId, role);
        return ResponseEntity.ok(new ApiResponse<>(true, "Team member added successfully", result));
    }

    /**
     * Removes a team member from a project.
     *
     * @param projectId the ID of the project
     * @param userId the ID of the user to remove
     * @return the updated project
     */
    @DeleteMapping("/{projectId}/team-members/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    public ResponseEntity<ApiResponse<ProjectDTO>> removeTeamMember(
            @PathVariable String projectId,
            @PathVariable String userId) {
        log.info("REST request to remove team member {} from Project: {}", userId, projectId);
        ProjectDTO result = projectService.removeTeamMember(projectId, userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Team member removed successfully", result));
    }

    /**
     * Updates a project's status.
     *
     * @param id the ID of the project
     * @param status the new status
     * @return the updated project
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    public ResponseEntity<ApiResponse<ProjectDTO>> updateProjectStatus(
            @PathVariable String id,
            @RequestParam String status) {
        log.info("REST request to update Project status: {} to {}", id, status);
        ProjectDTO result = projectService.updateProjectStatus(id, status);
        return ResponseEntity.ok(new ApiResponse<>(true, "Project status updated successfully", result));
    }

    /**
     * Retrieves all tasks for a project.
     *
     * @param projectId the ID of the project
     * @param pageable pagination information
     * @return the list of tasks
     */
    @GetMapping("/{projectId}/tasks")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER', 'USER')")
    public ResponseEntity<ApiResponse<Page<TaskDTO>>> getProjectTasks(
            @PathVariable String projectId,
            Pageable pageable) {
        log.info("REST request to get all Tasks for Project: {}", projectId);
        Page<TaskDTO> page = taskService.getTasksByProjectId(projectId, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Project tasks retrieved successfully", page));
    }

    /**
     * Retrieves project statistics.
     *
     * @param id the ID of the project
     * @return the project statistics
     */
    @GetMapping("/{id}/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    public ResponseEntity<ApiResponse<Object>> getProjectStatistics(@PathVariable String id) {
        log.info("REST request to get statistics for Project: {}", id);
        Object statistics = projectService.getProjectStatistics(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Project statistics retrieved successfully", statistics));
    }

    /**
     * Searches for projects based on criteria.
     *
     * @param query the search query
     * @param status the project status filter
     * @param startDate the start date filter
     * @param endDate the end date filter
     * @param pageable pagination information
     * @return the list of matching projects
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER', 'USER')")
    public ResponseEntity<ApiResponse<Page<ProjectDTO>>> searchProjects(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Pageable pageable) {
        log.info("REST request to search Projects with query: {}", query);
        Page<ProjectDTO> page = projectService.searchProjects(query, status, startDate, endDate, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Project search results", page));
    }
}