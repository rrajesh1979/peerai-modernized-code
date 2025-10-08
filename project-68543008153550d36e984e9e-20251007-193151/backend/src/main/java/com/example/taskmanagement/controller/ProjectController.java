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
 * Provides endpoints for CRUD operations and additional project management functionality.
 */
@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Project Management", description = "APIs for project operations")
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
               description = "Creates a new project with the provided details")
    @ApiResponse(responseCode = "201", description = "Project created successfully",
                 content = @Content(schema = @Schema(implementation = ProjectDTO.class)))
    public ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody ProjectDTO projectDTO) {
        log.info("REST request to create Project: {}", projectDTO);
        ProjectDTO result = projectService.createProject(projectDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Updates an existing project.
     *
     * @param id the id of the project to update
     * @param projectDTO the updated project data
     * @return the updated project
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    @Operation(summary = "Update an existing project", 
               description = "Updates a project with the provided details")
    @ApiResponse(responseCode = "200", description = "Project updated successfully",
                 content = @Content(schema = @Schema(implementation = ProjectDTO.class)))
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
     * Gets a project by id.
     *
     * @param id the id of the project to retrieve
     * @return the project
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a project by ID", 
               description = "Retrieves detailed information about a specific project")
    @ApiResponse(responseCode = "200", description = "Project found",
                 content = @Content(schema = @Schema(implementation = ProjectDTO.class)))
    @ApiResponse(responseCode = "404", description = "Project not found")
    public ResponseEntity<ProjectDTO> getProject(
            @Parameter(description = "Project ID", required = true) @PathVariable String id) {
        log.info("REST request to get Project: {}", id);
        ProjectDTO projectDTO = projectService.getProjectById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        return ResponseEntity.ok(projectDTO);
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
    @ApiResponse(responseCode = "200", description = "Projects retrieved successfully")
    public ResponseEntity<Page<ProjectSummaryDTO>> getAllProjects(Pageable pageable) {
        log.info("REST request to get a page of Projects");
        Page<ProjectSummaryDTO> page = projectService.getAllProjects(pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * Gets all projects for a specific organization.
     *
     * @param organizationId the organization id
     * @return list of projects
     */
    @GetMapping("/organization/{organizationId}")
    @Operation(summary = "Get projects by organization", 
               description = "Retrieves all projects belonging to a specific organization")
    @ApiResponse(responseCode = "200", description = "Projects retrieved successfully")
    public ResponseEntity<List<ProjectSummaryDTO>> getProjectsByOrganization(
            @Parameter(description = "Organization ID", required = true) @PathVariable String organizationId) {
        log.info("REST request to get Projects for Organization: {}", organizationId);
        List<ProjectSummaryDTO> projects = projectService.getProjectsByOrganization(organizationId);
        return ResponseEntity.ok(projects);
    }

    /**
     * Deletes a project by id.
     *
     * @param id the id of the project to delete
     * @return no content response
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a project", 
               description = "Deletes a project by ID")
    @ApiResponse(responseCode = "204", description = "Project deleted successfully")
    @ApiResponse(responseCode = "404", description = "Project not found")
    public ResponseEntity<Void> deleteProject(
            @Parameter(description = "Project ID", required = true) @PathVariable String id) {
        log.info("REST request to delete Project: {}", id);
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Adds a user to a project.
     *
     * @param projectId the project id
     * @param userId the user id to add
     * @return the updated project
     */
    @PostMapping("/{projectId}/users/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    @Operation(summary = "Add user to project", 
               description = "Assigns a user to a project")
    @ApiResponse(responseCode = "200", description = "User added to project successfully")
    public ResponseEntity<ProjectDTO> addUserToProject(
            @Parameter(description = "Project ID", required = true) @PathVariable String projectId,
            @Parameter(description = "User ID", required = true) @PathVariable String userId) {
        log.info("REST request to add User {} to Project {}", userId, projectId);
        ProjectDTO result = projectService.addUserToProject(projectId, userId);
        return ResponseEntity.ok(result);
    }

    /**
     * Removes a user from a project.
     *
     * @param projectId the project id
     * @param userId the user id to remove
     * @return the updated project
     */
    @DeleteMapping("/{projectId}/users/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    @Operation(summary = "Remove user from project", 
               description = "Removes a user from a project")
    @ApiResponse(responseCode = "200", description = "User removed from project successfully")
    public ResponseEntity<ProjectDTO> removeUserFromProject(
            @Parameter(description = "Project ID", required = true) @PathVariable String projectId,
            @Parameter(description = "User ID", required = true) @PathVariable String userId) {
        log.info("REST request to remove User {} from Project {}", userId, projectId);
        ProjectDTO result = projectService.removeUserFromProject(projectId, userId);
        return ResponseEntity.ok(result);
    }

    /**
     * Gets project statistics.
     *
     * @param id the project id
     * @return project statistics
     */
    @GetMapping("/{id}/statistics")
    @Operation(summary = "Get project statistics", 
               description = "Retrieves statistics for a specific project")
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    public ResponseEntity<Object> getProjectStatistics(
            @Parameter(description = "Project ID", required = true) @PathVariable String id) {
        log.info("REST request to get statistics for Project: {}", id);
        Object statistics = projectService.getProjectStatistics(id);
        return ResponseEntity.ok(statistics);
    }
}