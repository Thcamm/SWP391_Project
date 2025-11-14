package model.employee.technician;

import dao.inventory.WorkOrderPartDao;
import model.inventory.WorkOrderPartView;

import java.util.List;

public class TaskPartsVM {
    public TaskAssignment task;
    public List<WorkOrderPartView> parts;

    public List<PartOption> availableParts;
    public TaskAssignment getTask() {
        return task;
    }

    public void setTask(TaskAssignment task) {
        this.task = task;
    }

    public List<WorkOrderPartView> getParts() {
        return parts;
    }

    public void setParts(List<WorkOrderPartView> parts) {
        this.parts = parts;
    }

    public void setAvailableParts(List<PartOption> availableParts) {
        this.availableParts = availableParts;
    }

    public List<PartOption> getAvailableParts() {
        return availableParts;
    }
}
