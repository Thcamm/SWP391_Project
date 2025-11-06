package service.employee;

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

    public ServiceResult acceptTask(int technicianId, int assignmentId) {
        if (technicianId <= 0 || assignmentId <= 0) {
            return ServiceResult.error(MessageConstants.ERR003);
        }

        TaskAssignment task = technicianDAO.getTaskById(assignmentId);
        if (task == null) return ServiceResult.error(MessageConstants.ERR002);
        if (task.getAssignToTechID() != technicianId) return ServiceResult.error(MessageConstants.TASK009);
        if (task.getStatus() != TaskAssignment.TaskStatus.ASSIGNED) {
            return ServiceResult.error(MessageConstants.TASK006); // ví dụ: "Trạng thái hiện tại không cho phép thao tác này"
        }

        boolean ok = technicianDAO.updateTaskStatus(
                assignmentId, TaskAssignment.TaskStatus.IN_PROGRESS, LocalDateTime.now()
        );

        if (!ok) return ServiceResult.error(MessageConstants.TASK005);

        technicianDAO.logActivity(
                technicianId, TechnicianActivity.ActivityType.TASK_ACCEPTED, assignmentId,
                "Accepted task assignment ID: " + assignmentId
        );
        return ServiceResult.success(MessageConstants.TASK001);
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
