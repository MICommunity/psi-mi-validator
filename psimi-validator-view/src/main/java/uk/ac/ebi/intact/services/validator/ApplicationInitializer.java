package uk.ac.ebi.intact.services.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.services.validator.context.ValidatorWebContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * This listener will initialize the validatorWebContext of the application
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>22-Jun-2010</pre>
 */

public class ApplicationInitializer implements ServletContextListener {
    /**
     * Logging, logging!
     */
    private static final Log log = LogFactory.getLog(ApplicationInitializer.class);

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("#############################################");
        System.out.println("Server started");
        System.out.println("#############################################");
        System.out.println("Prepare ontology and validator rules");

        ValidatorWebContext.getInstance();
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("#############################################");
        System.out.println("Server shutting down");
        System.out.println("#############################################");
    }
}
