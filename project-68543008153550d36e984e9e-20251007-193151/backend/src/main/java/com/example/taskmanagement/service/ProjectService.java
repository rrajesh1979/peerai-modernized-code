package com.example.taskmanagement.service;

import com.example.taskmanagement.exception.ResourceNotFoundException;
import com.example.taskmanagement.model.Project;
import com.example.taskmanagement.repository.ProjectRepository;
import com.example.taskmanagement.repository.TaskRepository;
import com.example.taskmanagement.repository.OrganizationRepository;
import com.example.taskmanagement.dto.ProjectDTO;
import com.example.taskmanagement.dto.ProjectSummaryDTO;
import com.example.taskmanagement.mapper.ProjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing Project-related operations.
 * Handles CRUD operations and business logic for projects.
 */
@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final OrganizationRepository organizationRepository;
    private final ProjectMapper projectMapper;

    /**
     * Creates a new project.
     *
     * @param projectDTO The project data transfer object
     * @return The created project DTO
     */
    @Transactional
    public ProjectDTO createProject(@Valid @NotNull ProjectDTO projectDTO) {
        log.info("Creating new project with name: {}", projectDTO.getName());
        
        // Verify organization exists
        if (!organizationRepository.existsById(new ObjectId(projectDTO.getOrganizationId()))) {
            throw new ResourceNotFoundException("Organization not found with id: " + projectDTO.getOrganizationId());
        }
        
        Project project = projectMapper.toEntity(projectDTO);
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        
        Project savedProject = projectRepository.save(project);
        log.info("Project created successfully with id: {}", savedProject.getId());
        
        return projectMapper.toDto(savedProject);
    }

    /**
     * Retrieves a project by its ID.
     *
     * @param id The project ID
     * @return The project DTO
     * @throws ResourceNotFoundException if project not found
     */
    public ProjectDTO getProjectById(@NotNull String id) {
        log.debug("Fetching project with id: {}", id);
        
        Project project = projectRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        
        return projectMapper.toDto(project);
    }

    /**
     * Updates an existing project.
     *
     * @param id The project ID
     * @param projectDTO The updated project data
     * @return The updated project DTO
     * @throws ResourceNotFoundException if project not found
     */
    @Transactional
    public ProjectDTO updateProject(@NotNull String id, @Valid @NotNull ProjectDTO projectDTO) {
        log.info("Updating project with id: {}", id);
        
        Project existingProject = projectRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        
        // Update fields
        existingProject.setName(projectDTO.getName());
        existingProject.setDescription(projectDTO.getDescription());
        existingProject.setStatus(projectDTO.getStatus());
        existingProject.setStartDate(projectDTO.getStartDate());
        existingProject.setEndDate(projectDTO.getEndDate());
        existingProject.setUpdatedAt(LocalDateTime.now());
        
        // Don't update organizationId as it's a core relationship
        
        Project updatedProject = projectRepository.save(existingProject);
        log.info("Project updated successfully: {}", updatedProject.getId());
        
        return projectMapper.toDto(updatedProject);
    }

    /**
     * Deletes a project by its ID.
     *
     * @param id The project ID
     * @throws ResourceNotFoundException if project not found
     */
    @Transactional
    public void deleteProject(@NotNull String id) {
        log.info("Deleting project with id: {}", id);
        
        ObjectId objectId = new ObjectId(id);
        
        if (!projectRepository.existsById(objectId)) {
            throw new ResourceNotFoundException("Project not found with id: " + id);
        }
        
        // Delete associated tasks first to maintain referential integrity
        taskRepository.deleteByProjectId(objectId);
        log.debug("Deleted all tasks associated with project id: {}", id);
        
        projectRepository.deleteById(objectId);
        log.info("Project deleted successfully: {}", id);
    }

    /**
     * Retrieves all projects with pagination.
     *
     * @param pageable Pagination information
     * @return Page of project DTOs
     */
    public Page<ProjectDTO> getAllProjects(Pageable pageable) {
        log.debug("Fetching all projects with pagination: {}", pageable);
        
        return projectRepository.findAll(pageable)
                .map(projectMapper::toDto);
    }

    /**
     * Retrieves all projects for a specific organization.
     *
     * @param organizationId The organization ID
     * @param pageable Pagination information
     * @return Page of project DTOs
     */
    public Page<ProjectDTO> getProjectsByOrganization(@NotNull String organizationId, Pageable pageable) {
        log.debug("Fetching projects for organization id: {}", organizationId);
        
        ObjectId orgObjectId = new ObjectId(organizationId);
        
        // Verify organization exists
        if (!organizationRepository.existsById(orgObjectId)) {
            throw new ResourceNotFoundException("Organization not found with id: " + organizationId);
        }
        
        return projectRepository.findByOrganizationId(orgObjectId, pageable)
                .map(projectMapper::toDto);
    }

    /**
     * Retrieves project summaries for dashboard display.
     *
     * @param organizationId The organization ID
     * @return List of project summary DTOs
     */
    public List<ProjectSummaryDTO> getProjectSummaries(@NotNull String organizationId) {
        log.debug("Fetching project summaries for organization id: {}", organizationId);
        
        ObjectId orgObjectId = new ObjectId(organizationId);
        
        List<Project> projects = projectRepository.findByOrganizationId(orgObjectId);
        
        return projects.stream()
                .map(project -> {
                    ProjectSummaryDTO summary = new ProjectSummaryDTO();
                    summary.setId(project.getId().toString());
                    summary.setName(project.getName());
                    summary.setStatus(project.getStatus());
                    summary.setStartDate(project.getStartDate());
                    summary.setEndDate(project.getEndDate());
                    
                    // Count tasks by status
                    long totalTasks = taskRepository.countByProjectId(project.getId());
                    long completedTasks = taskRepository.countByProjectIdAndStatus(project.getId(), "COMPLETED");
                    
                    summary.setTotalTasks((int) totalTasks);
                    summary.setCompletedTasks((int) completedTasks);
                    
                    // Calculate completion percentage
                    double completionPercentage = totalTasks > 0 
                            ? (double) completedTasks / totalTasks * 100 
                            : 0.0;
                    summary.setCompletionPercentage(Math.round(completionPercentage * 10.0) / 10.0);
                    
                    return summary;
                })
                .collect(Collectors.toList());
    }

    /**
     * Checks if a project exists by ID.
     *
     * @param id The project ID
     * @return true if project exists, false otherwise
     */
    public boolean existsById(@NotNull String id) {
        return projectRepository.existsById(new ObjectId(id));
    }

    /**
     * Archives a project (marks it as archived without deleting).
     *
     * @param id The project ID
     * @return The updated project DTO
     */
    @Transactional
    public ProjectDTO archiveProject(@NotNull String id) {
        log.info("Archiving project with id: {}", id);
        
        Project project = projectRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        
        project.setStatus("ARCHIVED");
        project.setUpdatedAt(LocalDateTime.now());
        project.setArchivedAt(LocalDateTime.now());
        
        Project savedProject = projectRepository.save(project);
        log.info("Project archived successfully: {}", id);
        
        return projectMapper.toDto(savedProject);
    }

    /**
     * Searches for projects by name or description.
     *
     * @param query The search query
     * @param pageable Pagination information
     * @return Page of project DTOs matching the search criteria
     */
    public Page<ProjectDTO> searchProjects(@NotNull String query, Pageable pageable) {
        log.debug("Searching projects with query: {}", query);
        
        return projectRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query, pageable)
                .map(projectMapper::toDto);
    }
}