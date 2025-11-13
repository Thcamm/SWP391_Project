//package common.listener;
//
//import jakarta.servlet.ServletContextEvent;
//import jakarta.servlet.ServletContextListener;
//import jakarta.servlet.annotation.WebListener;
//import service.appointment.AppointmentService;
//
//import java.util.concurrent.*;
//
//@WebListener
//public class AppAutoJobListener implements ServletContextListener {
//    private ScheduledExecutorService scheduler;
//
//    @Override
//    public void contextInitialized(ServletContextEvent sce) {
//        scheduler = Executors.newScheduledThreadPool(1);
//        AppointmentService jobService = new AppointmentService();
//
//        Runnable job = () -> {
//            try {
//                jobService.processReminders();
//                jobService.processAutoReject();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        };
//
//        scheduler.scheduleAtFixedRate(job, 0, 24, TimeUnit.HOURS);
//        System.out.println(" Appointment job scheduler started.");
//    }
//
//    @Override
//    public void contextDestroyed(ServletContextEvent sce) {
//        if (scheduler != null) scheduler.shutdownNow();
//    }
//}
