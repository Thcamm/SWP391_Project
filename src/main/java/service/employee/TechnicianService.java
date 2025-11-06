package service.employee;

import common.DbContext;
import common.constant.MessageConstants;
import common.message.ServiceResult;
import common.utils.PaginationUtils;
import dao.employee.technician.TechnicianDAO;
import dao.vehicle.VehicleDiagnosticDAO;
import dao.workorder.WorkOrderDetailDAO;
import model.employee.Employee;
import model.employee.technician.TaskAssignment;
import model.employee.technician.TaskStatistics;
import model.employee.technician.TechnicianActivity;
import model.pagination.PaginationResponse;
import model.servicetype.Service;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class TechnicianService {

    private final TechnicianDAO technicianDAO;

    private WorkOrderDetailDAO workOrderDetailDAO = new WorkOrderDetailDAO();

    private VehicleDiagnosticDAO vehicleDiagnosticDAO = new VehicleDiagnosticDAO();

    public TechnicianService() {
        this.technicianDAO = new TechnicianDAO();
    }

    public ServiceResult getTechnicianByUserId(int userId) {
        if(userId <= 0) {
            return ServiceResult.error(MessageConstants.ERR003); //invalid input
        }

        Employee technician = technicianDAO.getTechnicianByUserId(userId);
        if(technician == null) {

            return ServiceResult.error(MessageConstants.ERR002); //data not found
        }

        return ServiceResult.success(null, technician);
    }

    public  ServiceResult getTaskStatistics(int technicianId) {
        if(technicianId <= 0) {
            return ServiceResult.error(MessageConstants.ERR003); //invalid input
        }

        var stats = technicianDAO.getTaskStatistics(technicianId);
        if(stats == null) {
            return ServiceResult.error(MessageConstants.ERR002); //data not found
        }

        return ServiceResult.success(null, stats);
    }

    public ServiceResult getTaskById(int technicianId, int assignmentId) {
        if (technicianId <= 0 || assignmentId <= 0) {
            return ServiceResult.error(MessageConstants.ERR003); // invalid inpur
        }
        TaskAssignment task = technicianDAO.getTaskById(assignmentId);
        if (task == null) {
            return ServiceResult.error(MessageConstants.ERR002); //data not found

        }

        if (task.getAssignToTechID() != technicianId) {
            return ServiceResult.error(MessageConstants.TASK009); // dont permission to change this task
        }
        return ServiceResult.success(null, task);
    }

    //task list with pagination

    public PaginationResponse<TaskAssignment> getNewAssignedTasks (int technicianId, int page, int itemsPerPage) {
        if(technicianId <= 0) {
            return createEmptyPaginationResponse(page, itemsPerPage);
        }

        List<TaskAssignment> allTasks = technicianDAO.getNewAssignedTasks(technicianId);

        PaginationUtils.PaginationResult<TaskAssignment> result =
                PaginationUtils.paginate(allTasks, page, itemsPerPage);

        return new PaginationResponse<>(result.getPaginatedData(),
                result.getCurrentPage(),
                result.getItemsPerPage(),
                result.getTotalItems(),
                result.getTotalPages()
        );

    }

    //Lay danh sach task dang lam vc
    public PaginationResponse<TaskAssignment> getInProgressTasks(int technicianId, int page, int itemsPerPage) {
        if(technicianId <= 0) {
            return createEmptyPaginationResponse(page, itemsPerPage);
        }

        List<TaskAssignment> allTasks = technicianDAO.getInProgressTasks(technicianId);

        PaginationUtils.PaginationResult<TaskAssignment> result =
                PaginationUtils.paginate(allTasks, page, itemsPerPage);

        return new PaginationResponse<>(result.getPaginatedData(),
                result.getCurrentPage(),
                result.getItemsPerPage(),
                result.getTotalItems(),
                result.getTotalPages()
        );

    }

    public PaginationResponse<TaskAssignment> getAllTasks(int technicianId, int page, int itemsPerPage) {
        if(technicianId <= 0) {
            return createEmptyPaginationResponse(page, itemsPerPage);
        }

        List<TaskAssignment> newTasks = technicianDAO.getNewAssignedTasks(technicianId);
        List<TaskAssignment> inProgressTasks = technicianDAO.getInProgressTasks(technicianId);

        newTasks.addAll(inProgressTasks);


        PaginationUtils.PaginationResult<TaskAssignment> result =
                PaginationUtils.paginate(newTasks, page, itemsPerPage);

        return new PaginationResponse<>(result.getPaginatedData(),
                result.getCurrentPage(),
                result.getItemsPerPage(),
                result.getTotalItems(),
                result.getTotalPages()
        );

    }

    public PaginationResponse<TaskAssignment> getAllTasksWithFilter(
            int technicianId,
            String status,
            String priority,
            String search,
            int page,
            int itemsPerPage
    ) {
        if (technicianId <= 0) {
            return createEmptyPaginationResponse(page, itemsPerPage);
        }

        int totalItems = technicianDAO.countAllTasksWithFilter(technicianId, status, priority, search);
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
        int offset = (page - 1) * itemsPerPage;

        List<TaskAssignment> tasks = technicianDAO.getAllTasksWithFilterPaged(
                technicianId, status, priority, search, offset, itemsPerPage
        );

        return new PaginationResponse<>(
                tasks, page, itemsPerPage, totalItems, totalPages
        );
    }


    public ServiceResult getAllTasksStatistics(int technicianId) {
        if(technicianId <= 0) {
            return ServiceResult.error(MessageConstants.ERR003);
        }
        TaskStatistics stats = technicianDAO.getAllTasksStatistics(technicianId);
        if(stats == null) {
            return ServiceResult.error(MessageConstants.ERR002);
        }

        return ServiceResult.success(null, stats);
    }


    public ServiceResult acceptTaskTx(int technicianId, int assignmentId) {
        if (technicianId <= 0 || assignmentId <= 0) {
            return ServiceResult.error(MessageConstants.ERR003);
        }

        try (Connection conn = DbContext.getConnection()) {
            // Transaction bắt đầu
            boolean oldAuto = conn.getAutoCommit();
            try {
                conn.setAutoCommit(false);
                conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

                // 1) Khóa hàng assignment + join đầy đủ để map UI
                TaskAssignment task = technicianDAO.getTaskForUpdate(conn, assignmentId);
                if (task == null) {
                    conn.rollback();
                    return ServiceResult.error(MessageConstants.ERR002);
                }
                if (task.getAssignToTechID() != technicianId) {
                    conn.rollback();
                    return ServiceResult.error(MessageConstants.TASK009);
                }
                if (task.getStatus() != TaskAssignment.TaskStatus.ASSIGNED) {
                    conn.rollback();
                    return ServiceResult.error(MessageConstants.TASK006);
                }

                // 2) Validate planned window
                LocalDateTime ps = task.getPlannedStart();
                LocalDateTime pe = task.getPlannedEnd();
                if (ps == null || pe == null || !pe.isAfter(ps)) {
                    conn.rollback();
                    return ServiceResult.error(MessageConstants.TASK014);
                }

                // 3) Check overlap tại thời điểm accept
                boolean hasOverlap = technicianDAO.hasOverlapAssignments(conn, technicianId, ps, pe, assignmentId);
                if (hasOverlap) {
                    conn.rollback();
                    return ServiceResult.error(MessageConstants.TASK013);
                }

                // 4) Check deadline accept (<= 10 phút sau planned_start)
                final int GRACE_MINUTES = 10;
                if (LocalDateTime.now().isAfter(ps.plusMinutes(GRACE_MINUTES))) {
                    conn.rollback();
                    return ServiceResult.error(MessageConstants.TASK012);
                }

                // 5) Update trạng thái có điều kiện (WHERE Status='ASSIGNED')
                LocalDateTime now = LocalDateTime.now();
                boolean ok = technicianDAO.updateTaskStatusWithStartTime(
                        conn,
                        assignmentId,
                        TaskAssignment.TaskStatus.ASSIGNED,
                        TaskAssignment.TaskStatus.IN_PROGRESS,
                        now
                );
                if (!ok) {
                    // có thể do race → đọc lại trạng thái
                    TaskAssignment latest = technicianDAO.getTaskForUpdate(conn, assignmentId);
                    conn.rollback();
                    if (latest != null && latest.getStatus() == TaskAssignment.TaskStatus.IN_PROGRESS) {
                        return ServiceResult.success(MessageConstants.TASK001);
                    }
                    return ServiceResult.error(MessageConstants.TASK005);
                }

                // 6) Log activity cùng transaction
                technicianDAO.logActivity(conn, technicianId, TechnicianActivity.ActivityType.TASK_ACCEPTED,
                        assignmentId, "Accepted task assignment ID: " + assignmentId);

                conn.commit();
                conn.setAutoCommit(oldAuto);
                return ServiceResult.success(MessageConstants.TASK001);

            } catch (SQLException ex) {
                try { conn.rollback(); } catch (SQLException ignore) {}
                return ServiceResult.error(MessageConstants.TASK005);
            } finally {
                try { conn.setAutoCommit(true); } catch (SQLException ignore) {}
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.error(MessageConstants.TASK005);
        }
    }



    public ServiceResult rejectTask (int technicianId, int assignmentId) {
        if (technicianId <= 0 || assignmentId <= 0) {
            return ServiceResult.error(MessageConstants.ERR003);//invalid input
        }

        boolean logged = technicianDAO.logActivity(
                technicianId,
                TechnicianActivity.ActivityType.TASK_REJECTED,
                assignmentId,
                "Rejected the assigned task"
        );

        if (logged) {
            return ServiceResult.success(MessageConstants.TASK002); // Task rejected successfully
        }

        return ServiceResult.error(MessageConstants.TASK005);
    }

    //update progess cua task
    public ServiceResult updateProgress(int technicianId, int assignmentId, int progressPercentage, String notes) {
        if (technicianId <= 0 || assignmentId <= 0) return ServiceResult.error(MessageConstants.ERR003);
        if (progressPercentage < 0 || progressPercentage > 100) return ServiceResult.error(MessageConstants.TASK007);

        TaskAssignment task = technicianDAO.getTaskById(assignmentId);
        if (task == null) return ServiceResult.error(MessageConstants.ERR002);
        if (task.getAssignToTechID() != technicianId) return ServiceResult.error(MessageConstants.TASK009);
        if (task.getStatus() != TaskAssignment.TaskStatus.IN_PROGRESS) {
            return ServiceResult.error(MessageConstants.TASK006);
        }

        boolean ok = technicianDAO.updateTaskProgress(assignmentId, progressPercentage, notes);
        if (!ok) return ServiceResult.error(MessageConstants.TASK005);

        technicianDAO.logActivity(
                technicianId, TechnicianActivity.ActivityType.PROGRESS_UPDATED, assignmentId,
                "Updated progress to " + progressPercentage + "%"
        );
        return ServiceResult.success(MessageConstants.TASK003);
    }


    public ServiceResult completeTask(int technicianId, int assignmentId, String notes) {
        if (technicianId <= 0 || assignmentId <= 0) return ServiceResult.error(MessageConstants.ERR003);

        TaskAssignment task = technicianDAO.getTaskById(assignmentId);
        if (task == null) return ServiceResult.error(MessageConstants.ERR002);
        if (task.getAssignToTechID() != technicianId) return ServiceResult.error(MessageConstants.TASK009);
        if (task.getStatus() != TaskAssignment.TaskStatus.IN_PROGRESS) {
            return ServiceResult.error(MessageConstants.TASK006);
        }

        // chặn khi còn chẩn đoán SUBMITTED nhưng parts chưa approved
        if (vehicleDiagnosticDAO.hasSubmittedWithPendingParts(assignmentId)) {
            return ServiceResult.error(MessageConstants.TASK012);
        }




//        if (workOrderDetailDAO.hasPendingApprovalOrOpenWorkOrder(assignmentId)) {
//            return ServiceResult.error(MessageConstants.WO_PENDING);
//        }


        boolean ok = technicianDAO.completeTask(assignmentId, notes);
        if (ok) {
            technicianDAO.logActivity(
                    technicianId, TechnicianActivity.ActivityType.TASK_COMPLETED, assignmentId,
                    "Completed task" + (notes != null && !notes.isEmpty() ? " with notes" : "")
            );


            workOrderDetailDAO.recomputeActualHoursAndMaybeMarkComplete(assignmentId);

            return ServiceResult.success(MessageConstants.TASK004);
        }
        return ServiceResult.error(MessageConstants.TASK005);
    }


    public PaginationResponse<TechnicianActivity> getRecentActivities(int technicianId, int page, int itemsPerPage) {
        if(technicianId <= 0) {
            return createEmptyPaginationResponse(page, itemsPerPage);
        }

        List<TechnicianActivity> allActivities = technicianDAO.getRecentActivities(technicianId, 50);

        PaginationUtils.PaginationResult<TechnicianActivity> result =
                PaginationUtils.paginate(allActivities, page, itemsPerPage);

        return new PaginationResponse<>(result.getPaginatedData(),
                result.getCurrentPage(),
                result.getItemsPerPage(),
                result.getTotalItems(),
                result.getTotalPages()
        );



    }

    private <T> PaginationResponse<T> createEmptyPaginationResponse(int page, int itemsPerPage) {
        return new PaginationResponse<>(
                List.of(),
                page,
                itemsPerPage,
                0,
                0
        );
    }
}
