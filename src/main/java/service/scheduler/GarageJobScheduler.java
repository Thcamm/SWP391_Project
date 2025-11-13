package service.scheduler;

import common.DbContext;
import dao.misc.NotificationDAO;
import dao.vehicle.VehicleDiagnosticDAO;
import model.misc.Notification;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * TASK 3 & 4: Automated Job Scheduler
 * 
 * This scheduler runs two background jobs:
 * 1. Auto-cancel overdue tasks (TASK 3)
 * 2. Auto-reject expired quotes (TASK 4)
 * 
 * Both jobs run every 1 minute.
 * 
 * @author SWP391 Team - Senior System Architect
 * @version 1.0
 */
public class GarageJobScheduler {

    private final ScheduledExecutorService scheduler;
    private final NotificationDAO notificationDAO;
    private final VehicleDiagnosticDAO diagnosticDAO;

    // Configuration constants
    private static final int TASK_TIMEOUT_MINUTES = 10; // TASK 3: KTV must start within 10 minutes
    private static final int QUOTE_TIMEOUT_MINUTES = 10; // TASK 4: Customer must respond within 10 minutes
    private static final int SCHEDULE_INTERVAL_MINUTES = 1; // Run every 1 minute

    public GarageJobScheduler() {
        this.scheduler = Executors.newScheduledThreadPool(2);
        this.notificationDAO = new NotificationDAO();
        this.diagnosticDAO = new VehicleDiagnosticDAO();
    }

    /**
     * Start the scheduler.
     * This should be called once during application startup.
     */
    public void start() {
        System.out.println("[GarageJobScheduler] Starting automated jobs...");

        // Schedule TASK 3: Auto-cancel overdue tasks
        scheduler.scheduleAtFixedRate(
                this::autoCancelOverdueTasks,
                0, // Initial delay
                SCHEDULE_INTERVAL_MINUTES,
                TimeUnit.MINUTES);

        // Schedule TASK 4: Auto-reject expired quotes
        scheduler.scheduleAtFixedRate(
                this::autoRejectExpiredQuotes,
                0, // Initial delay
                SCHEDULE_INTERVAL_MINUTES,
                TimeUnit.MINUTES);

        System.out.println("[GarageJobScheduler] Jobs started successfully.");
    }

    /**
     * Stop the scheduler.
     * This should be called during application shutdown.
     */
    public void stop() {
        System.out.println("[GarageJobScheduler] Stopping automated jobs...");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        System.out.println("[GarageJobScheduler] Jobs stopped.");
    }

    /**
     * TASK 3: Auto-cancel overdue tasks
     * 
     * Cancels tasks that meet ALL four criteria:
     * 1. Status = 'ASSIGNED'
     * 2. StartAt IS NULL (KTV has not started)
     * 3. planned_start IS NOT NULL
     * 4. planned_start < (NOW() - INTERVAL 10 MINUTE)
     * 
     * For each cancelled task, creates a notification for the TechManager.
     */
    private void autoCancelOverdueTasks() {
        String selectSql = "SELECT ta.AssignmentID, ta.AssignToTechID, ta.planned_start, " +
                "wod.WorkOrderID, wo.TechManagerID, e.UserID AS TechManagerUserID " +
                "FROM TaskAssignment ta " +
                "JOIN WorkOrderDetail wod ON ta.DetailID = wod.DetailID " +
                "JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID " +
                "JOIN Employee e ON wo.TechManagerID = e.EmployeeID " +
                "WHERE ta.Status = 'ASSIGNED' " +
                "AND ta.StartAt IS NULL " +
                "AND ta.planned_start IS NOT NULL " +
                "AND ta.planned_start < (NOW() - INTERVAL ? MINUTE)";

        String updateSql = "UPDATE TaskAssignment " +
                "SET Status = 'CANCELLED', " +
                "notes = CONCAT(COALESCE(notes, ''), '\\n[AUTO-CANCELLED] Technician did not start within 10 minutes of planned start time.') " +
                "WHERE AssignmentID = ?";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement selectPs = conn.prepareStatement(selectSql);
                PreparedStatement updatePs = conn.prepareStatement(updateSql)) {

            selectPs.setInt(1, TASK_TIMEOUT_MINUTES);

            try (ResultSet rs = selectPs.executeQuery()) {
                int cancelledCount = 0;

                while (rs.next()) {
                    int assignmentId = rs.getInt("AssignmentID");
                    int workOrderId = rs.getInt("WorkOrderID");
                    int techManagerUserId = rs.getInt("TechManagerUserID");

                    // Cancel the task
                    updatePs.setInt(1, assignmentId);
                    int rowsUpdated = updatePs.executeUpdate();

                    if (rowsUpdated > 0) {
                        cancelledCount++;

                        // Create notification for TechManager
                        try {
                            Notification notification = new Notification();
                            notification.setUserId(techManagerUserId);
                            notification.setTitle("Task Auto-Cancelled");
                            notification.setBody(String.format(
                                    "Task #%d (WorkOrder #%d) was automatically cancelled because the technician did not start within %d minutes of the planned start time.",
                                    assignmentId, workOrderId, TASK_TIMEOUT_MINUTES));
                            notification.setEntityType("TASK_ASSIGNMENT");
                            notification.setEntityId(assignmentId);

                            notificationDAO.createNotification(notification);

                            System.out.println(String.format(
                                    "[TASK 3] Auto-cancelled Task #%d (WorkOrder #%d), notified TechManager (UserID: %d)",
                                    assignmentId, workOrderId, techManagerUserId));

                        } catch (SQLException notifEx) {
                            System.err.println("[TASK 3] Failed to create notification for cancelled task #" +
                                    assignmentId + ": " + notifEx.getMessage());
                        }
                    }
                }

                if (cancelledCount > 0) {
                    System.out.println("[TASK 3] Auto-cancelled " + cancelledCount + " overdue tasks.");
                }
            }

        } catch (SQLException e) {
            System.err.println("[TASK 3] Error in autoCancelOverdueTasks: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * TASK 4: Auto-reject expired quotes
     * 
     * Rejects VehicleDiagnostic quotes that meet ALL criteria:
     * 1. Status = 'SUBMITTED'
     * 2. CreatedAt < (NOW() - INTERVAL 10 MINUTE)
     * 
     * Sets the quote to REJECTED and adds a RejectReason.
     * 
     * Note: 10 minutes is very strict for production. This interval may be
     * changed to 24-48 hours later, but the logic remains the same.
     */
    private void autoRejectExpiredQuotes() {
        String updateSql = "UPDATE VehicleDiagnostic " +
                "SET Status = 'REJECTED', " +
                "RejectReason = 'Auto-rejected due to customer non-response within 10 minutes.' " +
                "WHERE Status = 'SUBMITTED' " +
                "AND CreatedAt < (NOW() - INTERVAL ? MINUTE)";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement updatePs = conn.prepareStatement(updateSql)) {

            updatePs.setInt(1, QUOTE_TIMEOUT_MINUTES);

            int rowsUpdated = updatePs.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("[TASK 4] Auto-rejected " + rowsUpdated + " expired quotes.");
            }

        } catch (SQLException e) {
            System.err.println("[TASK 4] Error in autoRejectExpiredQuotes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Manual trigger for TASK 3 (for testing purposes).
     * 
     * @return number of tasks cancelled
     */
    public int manualTriggerCancelOverdueTasks() {
        System.out.println("[GarageJobScheduler] Manual trigger: autoCancelOverdueTasks");
        autoCancelOverdueTasks();
        return 0; // You can modify this to return actual count if needed
    }

    /**
     * Manual trigger for TASK 4 (for testing purposes).
     * 
     * @return number of quotes rejected
     */
    public int manualTriggerRejectExpiredQuotes() {
        System.out.println("[GarageJobScheduler] Manual trigger: autoRejectExpiredQuotes");
        autoRejectExpiredQuotes();
        return 0; // You can modify this to return actual count if needed
    }
}
