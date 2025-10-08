package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.ProjectDTO;
import com.example.taskmanagement.dto.ProjectSummaryDTO;
import com.example.taskmanagement.exception.ResourceNotFoundException;
import com.example.taskmanagement.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * Provides endpoints for CRUD operations and additional project-related functionality.
 */
@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Project Management", description = "APIs for managing projects")
public class ProjectController {

    private final ProjectService projectService;

    /**
     * Creates a new project.
     *
     * @param projectDTO the project to create
     * @return the created project with status 201
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    @Operation(summary = "Create a new project", 
               description = "Creates a new project with the provided details",
               responses = {
                   @ApiResponse(responseCode = "201", description = "Project created successfully"),
                   @ApiResponse(responseCode = "400", description = "Invalid input data"),
                   @ApiResponse(responseCode = "403", description = "Insufficient permissions")
               })
    public ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody ProjectDTO projectDTO) {
        log.info("REST request to create Project: {}", projectDTO);
        ProjectDTO result = projectService.createProject(projectDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Updates an existing project.
     *
     * @param id the ID of the project to update
     * @param projectDTO the updated project data
     * @return the updated project
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    @Operation(summary = "Update an existing project", 
               description = "Updates a project with the provided details",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Project updated successfully"),
                   @ApiResponse(responseCode = "400", description = "Invalid input data"),
                   @ApiResponse(responseCode = "404", description = "Project not found"),
                   @ApiResponse(responseCode = "403", description = "Insufficient permissions")
               })
    public ResponseEntity<ProjectDTO> updateProject(
            @Parameter(description = "Project ID", required = true) @PathVariable String id,
            @Valid @RequestBody ProjectDTO projectDTO) {
        log.info("REST request to update Project: {}", projectDTO);
        
        if (!id.equals(projectDTO.getId())) {
            throw new IllegalArgumentException("IDs don't match");
        }
        
        ProjectDTO result = projectService.updateProject(projectDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * Gets a project by ID.
     *
     * @param id the ID of the project to retrieve
     * @return the project
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a project by ID", 
               description = "Retrieves detailed information about a specific project",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Project found", 
                                content = @Content(schema = @Schema(implementation = ProjectDTO.class))),
                   @ApiResponse(responseCode = "404", description = "Project not found")
               })
    public ResponseEntity<ProjectDTO> getProject(
            @Parameter(description = "Project ID", required = true) @PathVariable String id) {
        log.info("REST request to get Project: {}", id);
        return projectService.getProjectById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
    }

    /**
     * Gets all projects with pagination.
     *
     * @param pageable pagination information
     * @return a page of projects
     */
    @GetMapping
    @Operation(summary = "Get all projects", 
               description = "Retrieves a paginated list of all projects")
    public ResponseEntity<Page<ProjectSummaryDTO>> getAllProjects(Pageable pageable) {
        log.info("REST request to get all Projects");
        Page<ProjectSummaryDTO> page = projectService.getAllProjects(pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * Gets all projects for a specific organization.
     *
     * @param organizationId the organization ID
     * @return list of projects belonging to the organization
     */
    @GetMapping("/organization/{organizationId}")
    @Operation(summary = "Get projects by organization", 
               description = "Retrieves all projects belonging to a specific organization")
    public ResponseEntity<List<ProjectSummaryDTO>> getProjectsByOrganization(
            @Parameter(description = "Organization ID", required = true) @PathVariable String organizationId) {
        log.info("REST request to get Projects for Organization: {}", organizationId);
        List<ProjectSummaryDTO> projects = projectService.getProjectsByOrganizationId(organizationId);
        return ResponseEntity.ok(projects);
    }

    /**
     * Deletes a project by ID.
     *
     * @param id the ID of the project to delete
     * @return no content response
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a project", 
               description = "Deletes a project by ID",
               responses = {
                   @ApiResponse(responseCode = "204", description = "Project deleted successfully"),
                   @ApiResponse(responseCode = "404", description = "Project not found"),
                   @ApiResponse(responseCode = "403", description = "Insufficient permissions")
               })
    public ResponseEntity<Void> deleteProject(
            @Parameter(description = "Project ID", required = true) @PathVariable String id) {
        log.info("REST request to delete Project: {}", id);
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Assigns a user to a project.
     *
     * @param projectId the project ID
     * @param userId the user ID to assign
     * @return the updated project
     */
    @PostMapping("/{projectId}/users/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    @Operation(summary = "Assign user to project", 
               description = "Assigns a user to a specific project")
    public ResponseEntity<ProjectDTO> assignUserToProject(
            @Parameter(description = "Project ID", required = true) @PathVariable String projectId,
            @Parameter(description = "User ID", required = true) @PathVariable String userId) {
        log.info("REST request to assign User: {} to Project: {}", userId, projectId);
        ProjectDTO result = projectService.assignUserToProject(projectId, userId);
        return ResponseEntity.ok(result);
    }

    /**
     * Removes a user from a project.
     *
     * @param projectId the project ID
     * @param userId the user ID to remove
     * @return the updated project
     */
    @DeleteMapping("/{projectId}/users/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    @Operation(summary = "Remove user from project", 
               description = "Removes a user from a specific project")
    public ResponseEntity<ProjectDTO> removeUserFromProject(
            @Parameter(description = "Project ID", required = true) @PathVariable String projectId,
            @Parameter(description = "User ID", required = true) @PathVariable String userId) {
        log.info("REST request to remove User: {} from Project: {}", userId, projectId);
        ProjectDTO result = projectService.removeUserFromProject(projectId, userId);
        return ResponseEntity.ok(result);
    }

    /**
     * Gets project statistics.
     *
     * @param projectId the project ID
     * @return project statistics
     */
    @GetMapping("/{projectId}/statistics")
    @Operation(summary = "Get project statistics", 
               description = "Retrieves statistics for a specific project")
    public ResponseEntity<Object> getProjectStatistics(
            @Parameter(description = "Project ID", required = true) @PathVariable String projectId) {
        log.info("REST request to get statistics for Project: {}", projectId);
        Object statistics = projectService.getProjectStatistics(projectId);
        return ResponseEntity.ok(statistics);
    }
}