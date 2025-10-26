package model.employee.technician;

public class TaskStatistics {
    private int newTasksCount;
    private int inProgressCount;
    private int completedTodayCount;
    private int pendingPartsCount;


    public TaskStatistics() {}

    public TaskStatistics(int newTasksCount, int inProgressCount,
                          int completedTodayCount, int pendingPartsCount) {
        this.newTasksCount = newTasksCount;
        this.inProgressCount = inProgressCount;
        this.completedTodayCount = completedTodayCount;
        this.pendingPartsCount = pendingPartsCount;
    }


    public int getNewTasksCount() { return newTasksCount; }
    public void setNewTasksCount(int newTasksCount) { this.newTasksCount = newTasksCount; }

    public int getInProgressCount() { return inProgressCount; }
    public void setInProgressCount(int inProgressCount) { this.inProgressCount = inProgressCount; }

    public int getCompletedTodayCount() { return completedTodayCount; }
    public void setCompletedTodayCount(int completedTodayCount) {
        this.completedTodayCount = completedTodayCount;
    }

    public int getPendingPartsCount() { return pendingPartsCount; }
    public void setPendingPartsCount(int pendingPartsCount) {
        this.pendingPartsCount = pendingPartsCount;
    }

    public int getTotalActiveTasks() {
        return newTasksCount + inProgressCount;
    }

    @Override
    public String toString() {
        return "TaskStatistics{" +
                "new=" + newTasksCount +
                ", inProgress=" + inProgressCount +
                ", completedToday=" + completedTodayCount +
                ", pendingParts=" + pendingPartsCount +
                '}';
    }
}