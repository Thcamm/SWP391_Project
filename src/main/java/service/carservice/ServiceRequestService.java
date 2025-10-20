package service.carservice;

import dao.misc.NotificationDAO;
import dao.carservice.ServiceRequestDAO;
import dao.user.UserDAO;
import model.misc.Notification;
import model.workorder.ServiceRequest;

import java.sql.SQLException;
import java.util.List;

public class ServiceRequestService {
    private final ServiceRequestDAO serviceRequestDAO = new ServiceRequestDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final UserDAO userDAO = new UserDAO();

    public boolean createRequestAndNotify(ServiceRequest request) throws SQLException {
        // 1. Create the Service Request and get its new ID
        int newRequestId = serviceRequestDAO.createServiceRequest(request);

        // If creation failed, stop here.
        if (newRequestId == -1) {
            return false;
        }

        // 2. Find all users with the 'Tech Manager' role
        List<Integer> managerIds = userDAO.findUserIdsByRoleName("Tech Manager");

        // 3. Create a notification for each Tech Manager
        for (Integer managerId : managerIds) {
            Notification notif = new Notification();
            notif.setUserId(managerId);
            notif.setTitle("New Service Request Created");
            notif.setBody("A new service request #" + newRequestId + " has been created and needs review.");
            notif.setEntityType("SERVICE_REQUEST");
            notif.setEntityId(newRequestId);

            // Send the notification
            notificationDAO.createNotification(notif);
        }

        return true;
    }
}