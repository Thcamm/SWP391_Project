package service.carservice;

import dao.carservice.ServiceRequestDAO;
import dao.misc.NotificationDAO;
import model.misc.Notification;
import model.workorder.ServiceRequest;

import java.sql.SQLException;
import java.util.List;

public class ServiceRequestService {

    private final ServiceRequestDAO serviceRequestDAO = new ServiceRequestDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();

    public boolean createRequestAndNotify(ServiceRequest request, List<Integer> recipientIds) throws SQLException {

        // --- BƯỚC 1: TẠO SERVICE REQUEST ---
        int newRequestId = serviceRequestDAO.createServiceRequest(request);

        if (newRequestId == -1) {
            return false;
        }

        // --- BƯỚC 2: GỬI THÔNG BÁO ---
        if (recipientIds != null && !recipientIds.isEmpty()) {

            for (Integer userId : recipientIds) {
                Notification notif = new Notification();

                notif.setUserId(userId);
                notif.setTitle("New Service Request Created");
                notif.setBody("A new service request #" + newRequestId + " needs review.");
                notif.setEntityType("SERVICE_REQUEST");
                notif.setEntityId(newRequestId);

                if (request.getAppointmentID() != null) {
                    notif.setAppointmentId(request.getAppointmentID());
                }

                notificationDAO.createNotification(notif);
            }
        }

        return true;
    }
}