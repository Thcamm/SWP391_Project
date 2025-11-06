package controller.scheduler;
import common.DbContext;
import dao.employee.technician.TechnicianDAO;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Connection;

@Component
public class AutoDeclineScheduler {

    private final TechnicianDAO technicianDAO;

    public AutoDeclineScheduler(TechnicianDAO technicianDAO) {
        this.technicianDAO = technicianDAO;
    }

    // chạy mỗi 60 giây
    @Scheduled(fixedDelay = 60_000)
    public void autoDeclineJob() {
        final int GRACE_MINUTES = 10;
        final int LOG_LOOKBACK_MIN = 15;

        try (Connection conn = DbContext.getConnection()) {
            boolean oldAuto = conn.getAutoCommit();
            conn.setAutoCommit(false);

            int affected = technicianDAO.cancelExpiredAssignments(conn, GRACE_MINUTES);
            if (affected > 0) {
                technicianDAO.logAutoCancelledAssignments(conn, LOG_LOOKBACK_MIN); // nếu bạn có hàm này
            }

            conn.commit();
            conn.setAutoCommit(oldAuto);
        } catch (Exception e) {
            // log lỗi cho dễ debug/alert
            e.printStackTrace();
        }
    }
}
