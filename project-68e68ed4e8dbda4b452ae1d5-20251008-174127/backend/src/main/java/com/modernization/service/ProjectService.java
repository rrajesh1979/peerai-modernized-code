package com.modernization.service;

import com.modernization.exception.ResourceNotFoundException;
import com.modernization.model.Project;
import com.modernization.model.Task;
import com.modernization.model.User;
import com.modernization.repository.ProjectRepository;
import com.modernization.repository.TaskRepository;
import com.modernization.repository.UserRepository;
import com.modernization.dto.ProjectDTO;
import com.modernization.dto.TeamMemberDTO;
import com.modernization.event.ProjectCreatedEvent;
import com.modernization.event.ProjectUpdatedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing projects.
 * Handles business logic related to project creation, updates, and team management.
 */
@Service
@Validated
public class ProjectService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public ProjectService(
            ProjectRepository projectRepository,
            UserRepository userRepository,
            TaskRepository taskRepository,
            ApplicationEventPublisher eventPublisher) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Creates a new project.
     *
     * @param projectDTO The project data transfer object containing project details
     * @return The created project
     */
    @Transactional
    public Project createProject(@Valid ProjectDTO projectDTO) {
        logger.info("Creating new project: {}", projectDTO.getName());
        
        // Validate owner exists
        User owner = userRepository.findById(projectDTO.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + projectDTO.getOwnerId()));
        
        Project project = new Project();
        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        project.setStatus(projectDTO.getStatus());
        project.setStartDate(projectDTO.getStartDate());
        project.setEndDate(projectDTO.getEndDate());
        project.setBudget(projectDTO.getBudget());
        project.setOwnerId(owner.getId());
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        
        // Process team members if provided
        if (projectDTO.getTeamMembers() != null && !projectDTO.getTeamMembers().isEmpty()) {
            List<Map<String, Object>> teamMembers = new ArrayList<>();
            
            for (TeamMemberDTO memberDTO : projectDTO.getTeamMembers()) {
                // Validate each team member exists
                userRepository.findById(memberDTO.getUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + memberDTO.getUserId()));
                
                Map<String, Object> member = Map.of(
                        "userId", memberDTO.getUserId(),
                        "role", memberDTO.getRole(),
                        "joinedAt", LocalDateTime.now()
                );
                teamMembers.add(member);
            }
            project.setTeamMembers(teamMembers);
        }
        
        Project savedProject = projectRepository.save(project);
        
        // Publish project created event
        eventPublisher.publishEvent(new ProjectCreatedEvent(savedProject));
        
        logger.info("Project created successfully with id: {}", savedProject.getId());
        return savedProject;
    }

    /**
     * Updates an existing project.
     *
     * @param id The project ID
     * @param projectDTO The project data transfer object containing updated details
     * @return The updated project
     */
    @Transactional
    public Project updateProject(@NotNull String id, @Valid ProjectDTO projectDTO) {
        logger.info("Updating project with id: {}", id);
        
        Project existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        
        // Update basic project information
        existingProject.setName(projectDTO.getName());
        existingProject.setDescription(projectDTO.getDescription());
        existingProject.setStatus(projectDTO.getStatus());
        existingProject.setStartDate(projectDTO.getStartDate());
        existingProject.setEndDate(projectDTO.getEndDate());
        existingProject.setBudget(projectDTO.getBudget());
        existingProject.setUpdatedAt(LocalDateTime.now());
        
        // Update team members if provided
        if (projectDTO.getTeamMembers() != null) {
            List<Map<String, Object>> teamMembers = new ArrayList<>();
            
            for (TeamMemberDTO memberDTO : projectDTO.getTeamMembers()) {
                // Validate each team member exists
                userRepository.findById(memberDTO.getUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + memberDTO.getUserId()));
                
                // Check if member already exists to preserve joinedAt date
                Optional<Map<String, Object>> existingMember = existingProject.getTeamMembers().stream()
                        .filter(m -> m.get("userId").equals(memberDTO.getUserId()))
                        .findFirst();
                
                LocalDateTime joinedAt = existingMember.isPresent() 
                        ? (LocalDateTime) existingMember.get().get("joinedAt") 
                        : LocalDateTime.now();
                
                Map<String, Object> member = Map.of(
                        "userId", memberDTO.getUserId(),
                        "role", memberDTO.getRole(),
                        "joinedAt", joinedAt
                );
                teamMembers.add(member);
            }
            existingProject.setTeamMembers(teamMembers);
        }
        
        Project updatedProject = projectRepository.save(existingProject);
        
        // Publish project updated event
        eventPublisher.publishEvent(new ProjectUpdatedEvent(updatedProject));
        
        logger.info("Project updated successfully: {}", updatedProject.getId());
        return updatedProject;
    }

    /**
     * Retrieves a project by its ID.
     *
     * @param id The project ID
     * @return The project
     * @throws ResourceNotFoundException if the project is not found
     */
    public Project getProjectById(@NotNull String id) {
        logger.debug("Fetching project with id: {}", id);
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
    }

    /**
     * Retrieves all projects with pagination.
     *
     * @param pageable Pagination information
     * @return A page of projects
     */
    public Page<Project> getAllProjects(Pageable pageable) {
        logger.debug("Fetching all projects with pagination: {}", pageable);
        return projectRepository.findAll(pageable);
    }

    /**
     * Retrieves projects owned by a specific user.
     *
     * @param userId The user ID
     * @param pageable Pagination information
     * @return A page of projects owned by the user
     */
    public Page<Project> getProjectsByOwnerId(@NotNull String userId, Pageable pageable) {
        logger.debug("Fetching projects owned by user: {}", userId);
        return projectRepository.findByOwnerId(userId, pageable);
    }

    /**
     * Retrieves projects where a user is a team member.
     *
     * @param userId The user ID
     * @param pageable Pagination information
     * @return A page of projects where the user is a team member
     */
    public Page<Project> getProjectsByTeamMember(@NotNull String userId, Pageable pageable) {
        logger.debug("Fetching projects where user {} is a team member", userId);
        return projectRepository.findByTeamMembersUserId(userId, pageable);
    }

    /**
     * Deletes a project by its ID.
     * Also deletes all associated tasks.
     *
     * @param id The project ID
     * @return true if the project was deleted, false otherwise
     */
    @Transactional
    public boolean deleteProject(@NotNull String id) {
        logger.info("Deleting project with id: {}", id);
        
        if (!projectRepository.existsById(id)) {
            logger.warn("Project not found with id: {}", id);
            return false;
        }
        
        // Delete associated tasks first
        List<Task> tasks = taskRepository.findByProjectId(id);
        if (!tasks.isEmpty()) {
            logger.info("Deleting {} tasks associated with project {}", tasks.size(), id);
            taskRepository.deleteAll(tasks);
        }
        
        projectRepository.deleteById(id);
        logger.info("Project deleted successfully: {}", id);
        return true;
    }

    /**
     * Adds a team member to a project.
     *
     * @param projectId The project ID
     * @param teamMemberDTO The team member details
     * @return The updated project
     */
    @Transactional
    public Project addTeamMember(@NotNull String projectId, @Valid TeamMemberDTO teamMemberDTO) {
        logger.info("Adding team member {} to project {}", teamMemberDTO.getUserId(), projectId);
        
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        
        User user = userRepository.findById(teamMemberDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + teamMemberDTO.getUserId()));
        
        // Check if user is already a team member
        boolean isMemberAlready = project.getTeamMembers().stream()
                .anyMatch(member -> member.get("userId").equals(teamMemberDTO.getUserId()));
        
        if (isMemberAlready) {
            logger.warn("User {} is already a team member of project {}", teamMemberDTO.getUserId(), projectId);
            return project;
        }
        
        // Add new team member
        List<Map<String, Object>> teamMembers = new ArrayList<>(project.getTeamMembers());
        Map<String, Object> newMember = Map.of(
                "userId", teamMemberDTO.getUserId(),
                "role", teamMemberDTO.getRole(),
                "joinedAt", LocalDateTime.now()
        );
        teamMembers.add(newMember);
        project.setTeamMembers(teamMembers);
        project.setUpdatedAt(LocalDateTime.now());
        
        Project updatedProject = projectRepository.save(project);
        logger.info("Team member added successfully to project {}", projectId);
        
        // Publish project updated event
        eventPublisher.publishEvent(new ProjectUpdatedEvent(updatedProject));
        
        return updatedProject;
    }

    /**
     * Removes a team member from a project.
     *
     * @param projectId The project ID
     * @param userId The user ID to remove
     * @return The updated project
     */
    @Transactional
    public Project removeTeamMember(@NotNull String projectId, @NotNull String userId) {
        logger.info("Removing team member {} from project {}", userId, projectId);
        
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        
        // Check if user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        
        // Filter out the team member to remove
        List<Map<String, Object>> updatedTeamMembers = project.getTeamMembers().stream()
                .filter(member -> !member.get("userId").equals(userId))
                .collect(Collectors.toList());
        
        // If no changes, user wasn't a team member
        if (updatedTeamMembers.size() == project.getTeamMembers().size()) {
            logger.warn("User {} is not a team member of project {}", userId, projectId);
            return project;
        }
        
        project.setTeamMembers(updatedTeamMembers);
        project.setUpdatedAt(LocalDateTime.now());
        
        Project updatedProject = projectRepository.save(project);
        logger.info("Team member removed successfully from project {}", projectId);
        
        // Publish project updated event
        eventPublisher.publishEvent(new ProjectUpdatedEvent(updatedProject));
        
        return updatedProject;
    }

    /**
     * Updates a project's status.
     *
     * @param projectId The project ID
     * @param status The new status
     * @return The updated project
     */
    @Transactional
    public Project updateProjectStatus(@NotNull String projectId, @NotNull String status) {
        logger.info("Updating status of project {} to {}", projectId, status);
        
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        
        project.setStatus(status);
        project.setUpdatedAt(LocalDateTime.now());
        
        Project updatedProject = projectRepository.save(project);
        
        // Publish project updated event
        eventPublisher.publishEvent(new ProjectUpdatedEvent(updatedProject));
        
        logger.info("Project status updated successfully: {}", projectId);
        return updatedProject;
    }

    /**
     * Searches for projects by name or description.
     *
     * @param searchTerm The search term
     * @param pageable Pagination information
     * @return A page of matching projects
     */
    public Page<Project> searchProjects(@NotNull String searchTerm, Pageable pageable) {
        logger.debug("Searching for projects with term: {}", searchTerm);
        return projectRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                searchTerm, searchTerm, pageable);
    }

    /**
     * Retrieves projects by status.
     *
     * @param status The project status
     * @param pageable Pagination information
     * @return A page of projects with the specified status
     */
    public Page<Project> getProjectsByStatus(@NotNull String status, Pageable pageable) {
        logger.debug("Fetching projects with status: {}", status);
        return projectRepository.findByStatus(status, pageable);
    }

    /**
     * Retrieves projects that are due to end within a specified number of days.
     *
     * @param days The number of days
     * @param pageable Pagination information
     * @return A page of projects ending soon
     */
    public Page<Project> getProjectsEndingSoon(int days, Pageable pageable) {
        logger.debug("Fetching projects ending within {} days", days);
        LocalDateTime endDate = LocalDateTime.now().plusDays(days);
        return projectRepository.findByEndDateBeforeAndStatusNot(endDate, "COMPLETED", pageable);
    }
}