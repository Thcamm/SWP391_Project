package service.inventory;

import common.message.ServiceResult;
import dao.employee.technician.TechnicianDAO;
import dao.inventory.WorkOrderPartDao;
import model.employee.technician.PartOption;
import model.employee.technician.TaskAssignment;
import model.employee.technician.TaskPartsVM;
import model.inventory.WorkOrderPartView;

import java.sql.SQLException;
import java.util.List;

public class PartRequestService {

    private final TechnicianDAO technicianDAO = new TechnicianDAO();
    private final WorkOrderPartDao workOrderPartDAO = new WorkOrderPartDao();

    public ServiceResult getPartsForAssignment(int assignmentId) {
        try {
            TaskAssignment task = technicianDAO.getTaskById(assignmentId);
            if (task == null) {
                return ServiceResult.error("ERR993", "Part", "Khong tim thay part nao");
            }

            List<WorkOrderPartView> parts = workOrderPartDAO.getPartsByAssignment(assignmentId);
            List<PartOption> options = workOrderPartDAO.getAvailablePartsForAssignment(assignmentId);

            TaskPartsVM vm = new TaskPartsVM();
            vm.task = task;
            vm.parts = parts;
            vm.availableParts = options;

            return ServiceResult.success(vm);
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.error("ERR157","Part","Lỗi khi tải danh sách phụ tùng.");
        }
    }

    public ServiceResult createPartRequest(int assignmentId, int partDetailId, int quantity) {
        try {
            TaskAssignment task = technicianDAO.getTaskById(assignmentId);
            if (task == null) {
                return ServiceResult.error("ERR288", "Task", "KO tim thay task");
            }

            if (task.getStatus() != TaskAssignment.TaskStatus.IN_PROGRESS) {
                return ServiceResult.error("ERR034", "Part", "Chi yeu cau phu tung khi task lad In_Progress");
            }

            // có thể thêm rule: task_type phải là REPAIR, v.v.
            return workOrderPartDAO.createPartRequestForAssignment(assignmentId, partDetailId, quantity);

        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.error("ERR754","Part","Không thể tạo yêu cầu phụ tùng.");
        }
    }
}

