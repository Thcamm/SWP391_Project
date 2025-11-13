package common.exception;

/**
 * Exception thrown when attempting to assign a task to a technician
 * who is already booked during the requested time slot.
 * 
 * This is part of TASK 2: Strict Overlap Validation
 * 
 * @author SWP391 Team
 * @version 1.0
 */
public class TechnicianScheduleConflictException extends Exception {

    private static final long serialVersionUID = 1L;

    private final int technicianId;
    private final String conflictingTaskInfo;

    public TechnicianScheduleConflictException(String message) {
        super(message);
        this.technicianId = 0;
        this.conflictingTaskInfo = null;
    }

    public TechnicianScheduleConflictException(String message, int technicianId, String conflictingTaskInfo) {
        super(message);
        this.technicianId = technicianId;
        this.conflictingTaskInfo = conflictingTaskInfo;
    }

    public int getTechnicianId() {
        return technicianId;
    }

    public String getConflictingTaskInfo() {
        return conflictingTaskInfo;
    }
}
