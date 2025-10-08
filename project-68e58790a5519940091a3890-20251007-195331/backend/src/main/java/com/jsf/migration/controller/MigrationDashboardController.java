package com.jsf.migration.controller;

import com.jsf.migration.dto.MigrationMetricsDTO;
import com.jsf.migration.dto.MigrationProgressDTO;
import com.jsf.migration.dto.MigrationRiskDTO;
import com.jsf.migration.dto.ProjectMigrationStatusDTO;
import com.jsf.migration.service.MigrationAnalyticsService;
import com.jsf.migration.service.MigrationProgressService;
import com.jsf.migration.service.MigrationRiskAssessmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controller for the Migration Analytics and Reporting Dashboard
 * Provides endpoints to track, measure, and visualize the progress and impact
 * of JSF modernization efforts across the organization.
 */
@RestController
@RequestMapping("/api/v1/migration/dashboard")
@RequiredArgsConstructor
@Slf4j
@Validated
public class MigrationDashboardController {

    private final MigrationProgressService progressService;
    private final MigrationAnalyticsService analyticsService;
    private final MigrationRiskAssessmentService riskService;

    /**
     * Retrieves overall migration progress metrics
     * 
     * @return Migration progress summary data
     */
    @GetMapping("/progress")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'DEVELOPER')")
    public ResponseEntity<MigrationProgressDTO> getMigrationProgress() {
        log.info("Fetching overall migration progress");
        try {
            MigrationProgressDTO progress = progressService.getOverallProgress();
            return ResponseEntity.ok(progress);
        } catch (Exception e) {
            log.error("Error retrieving migration progress", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieves migration progress for a specific project
     * 
     * @param projectId The ID of the project
     * @return Project-specific migration status
     */
    @GetMapping("/progress/project/{projectId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'DEVELOPER')")
    public ResponseEntity<ProjectMigrationStatusDTO> getProjectMigrationStatus(@PathVariable String projectId) {
        log.info("Fetching migration progress for project: {}", projectId);
        try {
            ProjectMigrationStatusDTO status = progressService.getProjectMigrationStatus(projectId);
            return ResponseEntity.ok(status);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid project ID requested: {}", projectId, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error retrieving project migration status for project: {}", projectId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieves performance metrics comparing JSF and migrated applications
     * 
     * @return Performance comparison metrics
     */
    @GetMapping("/metrics/performance")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<MigrationMetricsDTO> getPerformanceMetrics() {
        log.info("Fetching performance metrics comparison");
        try {
            MigrationMetricsDTO metrics = analyticsService.getPerformanceMetrics();
            return ResponseEntity.ok(metrics);
        } catch (Exception e) {
            log.error("Error retrieving performance metrics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieves technical debt reduction metrics
     * 
     * @return Technical debt metrics
     */
    @GetMapping("/metrics/technical-debt")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<MigrationMetricsDTO> getTechnicalDebtMetrics() {
        log.info("Fetching technical debt metrics");
        try {
            MigrationMetricsDTO metrics = analyticsService.getTechnicalDebtMetrics();
            return ResponseEntity.ok(metrics);
        } catch (Exception e) {
            log.error("Error retrieving technical debt metrics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieves migration cost analysis data
     * 
     * @return Cost analysis metrics
     */
    @GetMapping("/metrics/cost-analysis")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MigrationMetricsDTO> getCostAnalysisMetrics() {
        log.info("Fetching cost analysis metrics");
        try {
            MigrationMetricsDTO metrics = analyticsService.getCostAnalysisMetrics();
            return ResponseEntity.ok(metrics);
        } catch (Exception e) {
            log.error("Error retrieving cost analysis metrics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieves migration progress trends over time
     * 
     * @param startDate Optional start date for the trend data
     * @param endDate Optional end date for the trend data
     * @return Time series data of migration progress
     */
    @GetMapping("/trends/progress")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<LocalDate, Double>> getProgressTrends(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        log.info("Fetching migration progress trends from {} to {}", startDate, endDate);
        try {
            Map<LocalDate, Double> trends = progressService.getProgressTrends(startDate, endDate);
            return ResponseEntity.ok(trends);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid date range requested", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error retrieving migration progress trends", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieves risk assessment data for migration projects
     * 
     * @return List of risk assessments for all projects
     */
    @GetMapping("/risks")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<MigrationRiskDTO>> getMigrationRisks() {
        log.info("Fetching migration risk assessments");
        try {
            List<MigrationRiskDTO> risks = riskService.getAllRiskAssessments();
            return ResponseEntity.ok(risks);
        } catch (Exception e) {
            log.error("Error retrieving migration risk assessments", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieves risk assessment for a specific project
     * 
     * @param projectId The ID of the project
     * @return Risk assessment for the specified project
     */
    @GetMapping("/risks/project/{projectId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<MigrationRiskDTO> getProjectRiskAssessment(@PathVariable String projectId) {
        log.info("Fetching risk assessment for project: {}", projectId);
        try {
            MigrationRiskDTO risk = riskService.getProjectRiskAssessment(projectId);
            return ResponseEntity.ok(risk);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid project ID requested: {}", projectId, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error retrieving risk assessment for project: {}", projectId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Updates risk assessment for a specific project
     * 
     * @param projectId The ID of the project
     * @param riskDTO The updated risk assessment data
     * @return Updated risk assessment
     */
    @PutMapping("/risks/project/{projectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MigrationRiskDTO> updateProjectRiskAssessment(
            @PathVariable String projectId,
            @RequestBody @Validated MigrationRiskDTO riskDTO) {
        log.info("Updating risk assessment for project: {}", projectId);
        try {
            if (!projectId.equals(riskDTO.getProjectId())) {
                return ResponseEntity.badRequest().build();
            }
            MigrationRiskDTO updatedRisk = riskService.updateProjectRiskAssessment(riskDTO);
            return ResponseEntity.ok(updatedRisk);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid project ID or risk data: {}", projectId, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error updating risk assessment for project: {}", projectId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}