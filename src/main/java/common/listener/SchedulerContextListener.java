package common.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import service.scheduler.GarageJobScheduler;

/**
 * Application lifecycle listener that starts and stops the GarageJobScheduler.
 * 
 * This listener is automatically invoked when the web application starts and stops.
 * It ensures that the automated jobs (TASK 3 & 4) run throughout the application lifecycle.
 * 
 * @author SWP391 Team
 * @version 1.0
 */
@WebListener
public class SchedulerContextListener implements ServletContextListener {

    private GarageJobScheduler scheduler;

    /**
     * Called when the web application is initialized.
     * Starts the GarageJobScheduler.
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("[SchedulerContextListener] Application starting - initializing scheduler...");
        
        scheduler = new GarageJobScheduler();
        scheduler.start();
        
        // Store scheduler in ServletContext so it can be accessed elsewhere if needed
        sce.getServletContext().setAttribute("garageJobScheduler", scheduler);
        
        System.out.println("[SchedulerContextListener] Scheduler initialized successfully.");
    }

    /**
     * Called when the web application is shut down.
     * Stops the GarageJobScheduler gracefully.
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("[SchedulerContextListener] Application shutting down - stopping scheduler...");
        
        if (scheduler != null) {
            scheduler.stop();
        }
        
        // Remove from ServletContext
        sce.getServletContext().removeAttribute("garageJobScheduler");
        
        System.out.println("[SchedulerContextListener] Scheduler stopped successfully.");
    }
}
