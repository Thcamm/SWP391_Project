package service.support;

import dao.support.SupportDAO;
import model.support.SupportRequest;

public class SupportService {
    private final SupportDAO supportDAO;

    public SupportService() {
        this.supportDAO = new SupportDAO();
    }

    public boolean checkUpdateStatus(int requestID, String newStatus) throws Exception {
        SupportRequest req = supportDAO.getSupportRequestById(requestID);
        if (req == null) {
            throw new Exception("Support request not found for ID: " + requestID);
        }

        String currentStatus = req.getStatus();

        if ( "CLOSED".equalsIgnoreCase(currentStatus)) {
            throw new Exception("Cannot change the status of a request that is already " + currentStatus.toLowerCase());
        }

        if (!isValidTransition(currentStatus, newStatus)) {
            throw new Exception("Invalid status transition from " + currentStatus + " to " + newStatus);
        }

        return true;
    }
    private boolean isValidTransition(String current, String next) {
        switch (current.toUpperCase()) {
            case "PENDING":
                return next.equalsIgnoreCase("INPROGRESS");
            case "INPROGRESS":
                return next.equalsIgnoreCase("RESOLVED");
            default:
                return false;
        }
    }
}
