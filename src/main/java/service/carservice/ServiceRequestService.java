package service.carservice;

import dao.carservice.ServiceRequestDAO;
import dao.misc.NotificationDAO;
import model.misc.Notification;
import model.workorder.ServiceRequest;

import java.sql.SQLException;
import java.util.List;

public class ServiceRequestService {
    private final ServiceRequestDAO dao = new ServiceRequestDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();

    public boolean createRequestAndNotify(ServiceRequest request, List<Integer> serviceIds, List<Integer> recipientIds) throws SQLException {
        int requestId = dao.createServiceRequestWithDetails(request, serviceIds);
        if(requestId <= 0) return false;

        if(recipientIds != null) {
            for(Integer userId : recipientIds) {
                Notification notif = new Notification();
                notif.setUserId(userId);
                notif.setTitle("New Service Request Created");
                notif.setBody("A new service request #" + requestId + " needs review.");
                notif.setEntityType("SERVICE_REQUEST");
                notif.setEntityId(requestId);
                if(request.getAppointmentID()!=null) notif.setAppointmentId(request.getAppointmentID());
                notificationDAO.createNotification(notif);
            }
        }
        return true;
    }
}