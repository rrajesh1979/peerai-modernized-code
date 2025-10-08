package com.example.taskmanagement.service;

import com.example.taskmanagement.dto.ProjectDTO;
import com.example.taskmanagement.exception.ResourceNotFoundException;
import com.example.taskmanagement.model.Project;
import com.example.taskmanagement.repository.OrganizationRepository;
import com.example.taskmanagement.repository.ProjectRepository;
import com.example.taskmanagement.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing Project entities.
 * Handles business logic for project creation, retrieval, updates, and deletion.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final OrganizationRepository organizationRepository;
    private final TaskRepository taskRepository;

    /**
     * Creates a new project after validating the organization exists.
     *
     * @param projectDTO The project data transfer object containing project details
     * @return The created project
     * @throws ResourceNotFoundException if the referenced organization doesn't exist
     */
    @Transactional
    public Project createProject(ProjectDTO projectDTO) {
        log.info("Creating new project: {}", projectDTO.getName());
        
        // Validate organization exists
        organizationRepository.findById(projectDTO.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Organization not found with id: " + projectDTO.getOrganizationId()));
        
        Project project = Project.builder()
                .name(projectDTO.getName())
                .description(projectDTO.getDescription())
                .organizationId(projectDTO.getOrganizationId())
                .startDate(projectDTO.getStartDate())
                .endDate(projectDTO.getEndDate())
                .status(projectDTO.getStatus())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        return projectRepository.save(project);
    }

    /**
     * Retrieves a project by its ID.
     *
     * @param id The project ID
     * @return The project if found
     * @throws ResourceNotFoundException if the project doesn't exist
     */
    public Project getProjectById(String id) {
        log.debug("Fetching project with id: {}", id);
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
    }

    /**
     * Retrieves all projects with pagination support.
     *
     * @param pageable Pagination information
     * @return Page of projects
     */
    public Page<Project> getAllProjects(Pageable pageable) {
        log.debug("Fetching all projects with pagination: {}", pageable);
        return projectRepository.findAll(pageable);
    }

    /**
     * Retrieves all projects for a specific organization.
     *
     * @param organizationId The organization ID
     * @return List of projects belonging to the organization
     */
    public List<Project> getProjectsByOrganization(String organizationId) {
        log.debug("Fetching projects for organization: {}", organizationId);
        return projectRepository.findByOrganizationId(organizationId);
    }

    /**
     * Updates an existing project.
     *
     * @param id The project ID
     * @param projectDTO The updated project data
     * @return The updated project
     * @throws ResourceNotFoundException if the project doesn't exist
     */
    @Transactional
    public Project updateProject(String id, ProjectDTO projectDTO) {
        log.info("Updating project with id: {}", id);
        
        Project existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        
        // If organization ID is changing, validate the new organization exists
        if (projectDTO.getOrganizationId() != null && 
                !projectDTO.getOrganizationId().equals(existingProject.getOrganizationId())) {
            organizationRepository.findById(projectDTO.getOrganizationId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Organization not found with id: " + projectDTO.getOrganizationId()));
            existingProject.setOrganizationId(projectDTO.getOrganizationId());
        }
        
        // Update project fields if provided
        Optional.ofNullable(projectDTO.getName()).ifPresent(existingProject::setName);
        Optional.ofNullable(projectDTO.getDescription()).ifPresent(existingProject::setDescription);
        Optional.ofNullable(projectDTO.getStartDate()).ifPresent(existingProject::setStartDate);
        Optional.ofNullable(projectDTO.getEndDate()).ifPresent(existingProject::setEndDate);
        Optional.ofNullable(projectDTO.getStatus()).ifPresent(existingProject::setStatus);
        
        existingProject.setUpdatedAt(LocalDateTime.now());
        
        return projectRepository.save(existingProject);
    }

    /**
     * Deletes a project and all associated tasks.
     *
     * @param id The project ID
     * @throws ResourceNotFoundException if the project doesn't exist
     */
    @Transactional
    public void deleteProject(String id) {
        log.info("Deleting project with id: {}", id);
        
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        
        // Delete all tasks associated with this project
        taskRepository.deleteByProjectId(id);
        
        // Delete the project
        projectRepository.delete(project);
        
        log.info("Project deleted successfully: {}", id);
    }

    /**
     * Searches for projects by name (case-insensitive partial match).
     *
     * @param name The project name to search for
     * @param pageable Pagination information
     * @return Page of matching projects
     */
    public Page<Project> searchProjectsByName(String name, Pageable pageable) {
        log.debug("Searching projects with name containing: {}", name);
        return projectRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    /**
     * Counts the number of projects in an organization.
     *
     * @param organizationId The organization ID
     * @return The count of projects
     */
    public long countProjectsByOrganization(String organizationId) {
        return projectRepository.countByOrganizationId(organizationId);
    }
}