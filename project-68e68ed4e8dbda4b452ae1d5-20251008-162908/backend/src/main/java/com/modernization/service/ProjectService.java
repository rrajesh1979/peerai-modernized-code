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
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing project-related operations.
 * Handles CRUD operations and business logic for projects.
 */
@Service
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
     * Retrieves all projects with pagination support.
     *
     * @param pageable Pagination information
     * @return Page of projects
     */
    public Page<Project> getAllProjects(Pageable pageable) {
        logger.debug("Retrieving all projects with pagination: {}", pageable);
        return projectRepository.findAll(pageable);
    }
    
    /**
     * Retrieves projects owned by a specific user.
     *
     * @param ownerId The ID of the project owner
     * @param pageable Pagination information
     * @return Page of projects owned by the specified user
     */
    public Page<Project> getProjectsByOwner(String ownerId, Pageable pageable) {
        logger.debug("Retrieving projects for owner ID: {}", ownerId);
        return projectRepository.findByOwnerId(ownerId, pageable);
    }
    
    /**
     * Retrieves projects where a user is a team member.
     *
     * @param userId The ID of the team member
     * @param pageable Pagination information
     * @return Page of projects where the user is a team member
     */
    public Page<Project> getProjectsByTeamMember(String userId, Pageable pageable) {
        logger.debug("Retrieving projects where user ID: {} is a team member", userId);
        return projectRepository.findByTeamMembersUserId(userId, pageable);
    }
    
    /**
     * Retrieves a project by its ID.
     *
     * @param id The project ID
     * @return The project
     * @throws ResourceNotFoundException if the project is not found
     */
    public Project getProjectById(String id) {
        logger.debug("Retrieving project with ID: {}", id);
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
    }
    
    /**
     * Creates a new project.
     *
     * @param projectDTO The project data transfer object
     * @return The created project
     * @throws ResourceNotFoundException if the owner is not found
     */
    @Transactional
    public Project createProject(ProjectDTO projectDTO) {
        logger.info("Creating new project: {}", projectDTO.getName());
        
        // Validate owner exists
        User owner = userRepository.findById(projectDTO.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + projectDTO.getOwnerId()));
        
        Project project = new Project();
        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        project.setStatus(projectDTO.getStatus());
        project.setStartDate(projectDTO.getStartDate());
        project.setEndDate(projectDTO.getEndDate());
        project.setBudget(projectDTO.getBudget());
        project.setOwnerId(projectDTO.getOwnerId());
        
        // Process team members if provided
        if (projectDTO.getTeamMembers() != null && !projectDTO.getTeamMembers().isEmpty()) {
            project.setTeamMembers(processTeamMembers(projectDTO.getTeamMembers()));
        } else {
            project.setTeamMembers(new ArrayList<>());
        }
        
        Project savedProject = projectRepository.save(project);
        
        // Publish project created event
        eventPublisher.publishEvent(new ProjectCreatedEvent(this, savedProject));
        
        logger.info("Project created successfully with ID: {}", savedProject.getId());
        return savedProject;
    }
    
    /**
     * Updates an existing project.
     *
     * @param id The project ID
     * @param projectDTO The project data transfer object
     * @return The updated project
     * @throws ResourceNotFoundException if the project or owner is not found
     */
    @Transactional
    public Project updateProject(String id, ProjectDTO projectDTO) {
        logger.info("Updating project with ID: {}", id);
        
        Project existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        
        // Validate owner exists if owner is being changed
        if (!existingProject.getOwnerId().equals(projectDTO.getOwnerId())) {
            userRepository.findById(projectDTO.getOwnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + projectDTO.getOwnerId()));
        }
        
        // Update project fields
        existingProject.setName(projectDTO.getName());
        existingProject.setDescription(projectDTO.getDescription());
        existingProject.setStatus(projectDTO.getStatus());
        existingProject.setStartDate(projectDTO.getStartDate());
        existingProject.setEndDate(projectDTO.getEndDate());
        existingProject.setBudget(projectDTO.getBudget());
        existingProject.setOwnerId(projectDTO.getOwnerId());
        
        // Process team members if provided
        if (projectDTO.getTeamMembers() != null) {
            existingProject.setTeamMembers(processTeamMembers(projectDTO.getTeamMembers()));
        }
        
        Project updatedProject = projectRepository.save(existingProject);
        
        // Publish project updated event
        eventPublisher.publishEvent(new ProjectUpdatedEvent(this, updatedProject));
        
        logger.info("Project updated successfully: {}", updatedProject.getId());
        return updatedProject;
    }
    
    /**
     * Deletes a project by its ID.
     *
     * @param id The project ID
     * @throws ResourceNotFoundException if the project is not found
     */
    @Transactional
    public void deleteProject(String id) {
        logger.info("Deleting project with ID: {}", id);
        
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        
        // Delete associated tasks
        taskRepository.deleteByProjectId(id);
        
        // Delete the project
        projectRepository.delete(project);
        
        logger.info("Project and associated tasks deleted successfully: {}", id);
    }
    
    /**
     * Updates the status of a project.
     *
     * @param id The project ID
     * @param status The new status
     * @return The updated project
     * @throws ResourceNotFoundException if the project is not found
     */
    @Transactional
    public Project updateProjectStatus(String id, String status) {
        logger.info("Updating status of project ID: {} to {}", id, status);
        
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        
        project.setStatus(status);
        Project updatedProject = projectRepository.save(project);
        
        // Publish project updated event
        eventPublisher.publishEvent(new ProjectUpdatedEvent(this, updatedProject));
        
        logger.info("Project status updated successfully: {}", id);
        return updatedProject;
    }
    
    /**
     * Adds a team member to a project.
     *
     * @param projectId The project ID
     * @param teamMemberDTO The team member data transfer object
     * @return The updated project
     * @throws ResourceNotFoundException if the project or user is not found
     */
    @Transactional
    public Project addTeamMember(String projectId, TeamMemberDTO teamMemberDTO) {
        logger.info("Adding team member to project ID: {}", projectId);
        
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        
        // Validate user exists
        User user = userRepository.findById(teamMemberDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + teamMemberDTO.getUserId()));
        
        // Check if user is already a team member
        boolean userExists = project.getTeamMembers().stream()
                .anyMatch(member -> member.getUserId().equals(teamMemberDTO.getUserId()));
        
        if (userExists) {
            logger.warn("User {} is already a team member of project {}", teamMemberDTO.getUserId(), projectId);
            return project;
        }
        
        // Add new team member
        Project.TeamMember teamMember = new Project.TeamMember();
        teamMember.setUserId(teamMemberDTO.getUserId());
        teamMember.setRole(teamMemberDTO.getRole());
        teamMember.setJoinedDate(LocalDate.now());
        
        project.getTeamMembers().add(teamMember);
        Project updatedProject = projectRepository.save(project);
        
        // Publish project updated event
        eventPublisher.publishEvent(new ProjectUpdatedEvent(this, updatedProject));
        
        logger.info("Team member added successfully to project: {}", projectId);
        return updatedProject;
    }
    
    /**
     * Removes a team member from a project.
     *
     * @param projectId The project ID
     * @param userId The user ID of the team member to remove
     * @return The updated project
     * @throws ResourceNotFoundException if the project is not found
     */
    @Transactional
    public Project removeTeamMember(String projectId, String userId) {
        logger.info("Removing team member {} from project ID: {}", userId, projectId);
        
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        
        // Remove team member
        boolean removed = project.getTeamMembers().removeIf(member -> member.getUserId().equals(userId));
        
        if (!removed) {
            logger.warn("User {} is not a team member of project {}", userId, projectId);
            return project;
        }
        
        Project updatedProject = projectRepository.save(project);
        
        // Publish project updated event
        eventPublisher.publishEvent(new ProjectUpdatedEvent(this, updatedProject));
        
        logger.info("Team member removed successfully from project: {}", projectId);
        return updatedProject;
    }
    
    /**
     * Updates a team member's role in a project.
     *
     * @param projectId The project ID
     * @param userId The user ID of the team member
     * @param role The new role
     * @return The updated project
     * @throws ResourceNotFoundException if the project or team member is not found
     */
    @Transactional
    public Project updateTeamMemberRole(String projectId, String userId, String role) {
        logger.info("Updating role of team member {} in project ID: {} to {}", userId, projectId, role);
        
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        
        // Find and update team member
        Optional<Project.TeamMember> teamMemberOpt = project.getTeamMembers().stream()
                .filter(member -> member.getUserId().equals(userId))
                .findFirst();
        
        if (teamMemberOpt.isEmpty()) {
            throw new ResourceNotFoundException("Team member not found with userId: " + userId);
        }
        
        teamMemberOpt.get().setRole(role);
        Project updatedProject = projectRepository.save(project);
        
        // Publish project updated event
        eventPublisher.publishEvent(new ProjectUpdatedEvent(this, updatedProject));
        
        logger.info("Team member role updated successfully in project: {}", projectId);
        return updatedProject;
    }
    
    /**
     * Searches for projects based on various criteria.
     *
     * @param name Project name (partial match)
     * @param status Project status
     * @param startDateFrom Start date range from
     * @param startDateTo Start date range to
     * @param ownerId Owner ID
     * @param pageable Pagination information
     * @return Page of projects matching the criteria
     */
    public Page<Project> searchProjects(
            String name,
            String status,
            LocalDate startDateFrom,
            LocalDate startDateTo,
            String ownerId,
            Pageable pageable) {
        
        logger.debug("Searching projects with criteria - name: {}, status: {}, dateRange: {} to {}, ownerId: {}",
                name, status, startDateFrom, startDateTo, ownerId);
        
        // Use repository method for search
        return projectRepository.findBySearchCriteria(
                StringUtils.hasText(name) ? name : null,
                StringUtils.hasText(status) ? status : null,
                startDateFrom,
                startDateTo,
                StringUtils.hasText(ownerId) ? ownerId : null,
                pageable);
    }
    
    /**
     * Gets project statistics including task counts by status.
     *
     * @param projectId The project ID
     * @return Map containing project statistics
     * @throws ResourceNotFoundException if the project is not found
     */
    public Map<String, Object> getProjectStatistics(String projectId) {
        logger.debug("Getting statistics for project ID: {}", projectId);
        
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        
        // Get tasks for the project
        List<Task> tasks = taskRepository.findByProjectId(projectId);
        
        // Calculate task statistics
        Map<String, Long> taskStatusCounts = tasks.stream()
                .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()));
        
        // Calculate completion percentage
        long completedTasks = taskStatusCounts.getOrDefault("COMPLETED", 0L);
        double completionPercentage = tasks.isEmpty() ? 0 : (double) completedTasks / tasks.size() * 100;
        
        // Calculate days remaining
        long daysRemaining = 0;
        if (project.getEndDate() != null) {
            daysRemaining = LocalDate.now().until(project.getEndDate()).getDays();
            daysRemaining = Math.max(0, daysRemaining); // Ensure non-negative
        }
        
        // Compile statistics
        Map<String, Object> statistics = Map.of(
                "taskStatusCounts", taskStatusCounts,
                "totalTasks", tasks.size(),
                "completionPercentage", completionPercentage,
                "daysRemaining", daysRemaining,
                "budget", project.getBudget(),
                "teamSize", project.getTeamMembers().size()
        );
        
        logger.debug("Project statistics calculated for project ID: {}", projectId);
        return statistics;
    }
    
    /**
     * Updates the budget of a project.
     *
     * @param id The project ID
     * @param budget The new budget
     * @return The updated project
     * @throws ResourceNotFoundException if the project is not found
     */
    @Transactional
    public Project updateProjectBudget(String id, BigDecimal budget) {
        logger.info("Updating budget of project ID: {} to {}", id, budget);
        
        if (budget == null || budget.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Budget must be a non-negative value");
        }
        
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        
        project.setBudget(budget);
        Project updatedProject = projectRepository.save(project);
        
        // Publish project updated event
        eventPublisher.publishEvent(new ProjectUpdatedEvent(this, updatedProject));
        
        logger.info("Project budget updated successfully: {}", id);
        return updatedProject;
    }
    
    /**
     * Updates the date range of a project.
     *
     * @param id The project ID
     * @param startDate The new start date
     * @param endDate The new end date
     * @return The updated project
     * @throws ResourceNotFoundException if the project is not found
     * @throws IllegalArgumentException if the date range is invalid
     */
    @Transactional
    public Project updateProjectDates(String id, LocalDate startDate, LocalDate endDate) {
        logger.info("Updating dates of project ID: {} to {} - {}", id, startDate, endDate);
        
        // Validate date range
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        
        project.setStartDate(startDate);
        project.setEndDate(endDate);
        Project updatedProject = projectRepository.save(project);
        
        // Publish project updated event
        eventPublisher.publishEvent(new ProjectUpdatedEvent(this, updatedProject));
        
        logger.info("Project dates updated successfully: {}", id);
        return updatedProject;
    }
    
    /**
     * Processes team member DTOs and converts them to entity objects.
     *
     * @param teamMemberDTOs List of team member DTOs
     * @return List of team member entity objects
     */
    private List<Project.TeamMember> processTeamMembers(List<TeamMemberDTO> teamMemberDTOs) {
        List<Project.TeamMember> teamMembers = new ArrayList<>();
        
        for (TeamMemberDTO dto : teamMemberDTOs) {
            // Validate user exists
            userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getUserId()));
            
            Project.TeamMember teamMember = new Project.TeamMember();
            teamMember.setUserId(dto.getUserId());
            teamMember.setRole(dto.getRole());
            teamMember.setJoinedDate(dto.getJoinedDate() != null ? dto.getJoinedDate() : LocalDate.now());
            
            teamMembers.add(teamMember);
        }
        
        return teamMembers;
    }
}