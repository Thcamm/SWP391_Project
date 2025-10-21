package service.employee;

import common.constant.MessageConstants;
import common.message.ServiceResult;
import common.utils.PaginationUtils;
import dao.employee.technician.TechnicianDAO;
import model.employee.Employee;
import model.employee.technician.TaskAssignment;
import model.employee.technician.TechnicianActivity;
import model.pagination.PaginationResponse;
import model.servicetype.Service;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.List;

public class TechnicianService {

    private final TechnicianDAO technicianDAO;

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

    public ServiceResult acceptTask (int technocianId, int assignmentId) {
        if(technocianId <= 0 || assignmentId <= 0) {
            return ServiceResult.error(MessageConstants.ERR003);//invalid input
        }

        boolean success = technicianDAO.updateTaskStatus(assignmentId,
                TaskAssignment.TaskStatus.IN_PROGRESS,
                LocalDateTime.now()
        );

        if(success) {
            technicianDAO.logActivity(
                    technocianId,
                    TechnicianActivity.ActivityType.TASK_ACCEPTED,
                    assignmentId,
                    "Accepted task assignment ID: " + assignmentId);
            return ServiceResult.success(MessageConstants.TASK001); // Task accepted successfully
        }

        return ServiceResult.error(MessageConstants.TASK005); //faild to update task success
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
    public ServiceResult updateProgress(int techinicianId, int assignmentId, int progressPercentage, String notes) {
        if(techinicianId <= 0 || assignmentId <= 0) {
            return ServiceResult.error(MessageConstants.ERR003);
        }

        if(progressPercentage < 0 || progressPercentage > 100) {
            return ServiceResult.error(MessageConstants.TASK007); //invalid input
        }

        boolean success = technicianDAO.updateTaskProgress(assignmentId, progressPercentage, notes);

        if(success) {
            technicianDAO.logActivity(
                    techinicianId,
                    TechnicianActivity.ActivityType.PROGRESS_UPDATED,
                    assignmentId,
                    "Updated progress to " + progressPercentage + "%"
            );
            return ServiceResult.success(MessageConstants.TASK003); // Progress updated successfully

        }

        return ServiceResult.error(MessageConstants.TASK005);
    }

    public ServiceResult completeTask(int technicianId, int assignmentId, String notes) {
        if (technicianId <= 0 || assignmentId <= 0) {
            return ServiceResult.error(MessageConstants.ERR003);
        }

        boolean success = technicianDAO.completeTask(assignmentId, notes);
        if (success) {
            technicianDAO.logActivity(
                    technicianId,
                    TechnicianActivity.ActivityType.TASK_COMPLETED,
                    assignmentId,
                    "Completed task" + (notes != null && !notes.isEmpty() ? " with notes" : "")
            );

            return ServiceResult.success(MessageConstants.TASK004); // Task completed successfully
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
