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
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing Project entities.
 * Handles business logic related to projects including CRUD operations
 * and relationship management with organizations and tasks.
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
     * @param projectDTO the project data transfer object
     * @return the created project
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
     * @param id the project ID
     * @return the project
     * @throws ResourceNotFoundException if the project doesn't exist
     */
    public Project getProjectById(String id) {
        log.debug("Fetching project with id: {}", id);
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
    }

    /**
     * Updates an existing project.
     *
     * @param id the project ID
     * @param projectDTO the updated project data
     * @return the updated project
     * @throws ResourceNotFoundException if the project or referenced organization doesn't exist
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
        
        // Update fields if provided
        if (StringUtils.hasText(projectDTO.getName())) {
            existingProject.setName(projectDTO.getName());
        }
        
        if (projectDTO.getDescription() != null) {
            existingProject.setDescription(projectDTO.getDescription());
        }
        
        if (projectDTO.getStartDate() != null) {
            existingProject.setStartDate(projectDTO.getStartDate());
        }
        
        if (projectDTO.getEndDate() != null) {
            existingProject.setEndDate(projectDTO.getEndDate());
        }
        
        if (projectDTO.getStatus() != null) {
            existingProject.setStatus(projectDTO.getStatus());
        }
        
        existingProject.setUpdatedAt(LocalDateTime.now());
        
        return projectRepository.save(existingProject);
    }

    /**
     * Deletes a project and all associated tasks.
     *
     * @param id the project ID
     * @throws ResourceNotFoundException if the project doesn't exist
     */
    @Transactional
    public void deleteProject(String id) {
        log.info("Deleting project with id: {}", id);
        
        // Verify project exists
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project not found with id: " + id);
        }
        
        // Delete associated tasks first
        taskRepository.deleteByProjectId(id);
        
        // Delete the project
        projectRepository.deleteById(id);
        
        log.info("Project and associated tasks successfully deleted for project id: {}", id);
    }

    /**
     * Retrieves all projects with pagination support.
     *
     * @param pageable pagination information
     * @return a page of projects
     */
    public Page<Project> getAllProjects(Pageable pageable) {
        log.debug("Fetching all projects with pagination");
        return projectRepository.findAll(pageable);
    }

    /**
     * Retrieves all projects for a specific organization.
     *
     * @param organizationId the organization ID
     * @return list of projects belonging to the organization
     */
    public List<Project> getProjectsByOrganization(String organizationId) {
        log.debug("Fetching projects for organization id: {}", organizationId);
        
        // Verify organization exists
        if (!organizationRepository.existsById(organizationId)) {
            throw new ResourceNotFoundException("Organization not found with id: " + organizationId);
        }
        
        return projectRepository.findByOrganizationId(organizationId);
    }

    /**
     * Searches for projects by name containing the search term.
     *
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return a page of matching projects
     */
    public Page<Project> searchProjectsByName(String searchTerm, Pageable pageable) {
        log.debug("Searching projects with term: {}", searchTerm);
        return projectRepository.findByNameContainingIgnoreCase(searchTerm, pageable);
    }

    /**
     * Retrieves projects by status.
     *
     * @param status the project status
     * @param pageable pagination information
     * @return a page of projects with the specified status
     */
    public Page<Project> getProjectsByStatus(String status, Pageable pageable) {
        log.debug("Fetching projects with status: {}", status);
        return projectRepository.findByStatus(status, pageable);
    }

    /**
     * Checks if a project exists by ID.
     *
     * @param id the project ID
     * @return true if the project exists, false otherwise
     */
    public boolean existsById(String id) {
        return projectRepository.existsById(id);
    }

    /**
     * Retrieves projects that are due to end within the specified number of days.
     *
     * @param days number of days from now
     * @return list of projects ending within the specified timeframe
     */
    public List<Project> getProjectsEndingSoon(int days) {
        LocalDateTime endDate = LocalDateTime.now().plusDays(days);
        log.debug("Fetching projects ending before: {}", endDate);
        return projectRepository.findByEndDateBefore(endDate);
    }

    /**
     * Assigns a user to a project.
     *
     * @param projectId the project ID
     * @param userId the user ID to assign
     * @return the updated project
     * @throws ResourceNotFoundException if the project doesn't exist
     */
    @Transactional
    public Project assignUserToProject(String projectId, String userId) {
        log.info("Assigning user {} to project {}", userId, projectId);
        
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        
        // Add user to project members if not already present
        if (project.getMembers() == null || !project.getMembers().contains(userId)) {
            project.getMembers().add(userId);
            project.setUpdatedAt(LocalDateTime.now());
            return projectRepository.save(project);
        }
        
        return project;
    }

    /**
     * Removes a user from a project.
     *
     * @param projectId the project ID
     * @param userId the user ID to remove
     * @return the updated project
     * @throws ResourceNotFoundException if the project doesn't exist
     */
    @Transactional
    public Project removeUserFromProject(String projectId, String userId) {
        log.info("Removing user {} from project {}", userId, projectId);
        
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        
        // Remove user from project members if present
        if (project.getMembers() != null && project.getMembers().contains(userId)) {
            project.getMembers().remove(userId);
            project.setUpdatedAt(LocalDateTime.now());
            return projectRepository.save(project);
        }
        
        return project;
    }
}