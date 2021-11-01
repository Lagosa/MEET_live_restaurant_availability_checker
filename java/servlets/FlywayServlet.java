package blank.meet.server.servlets;

import blank.meet.server.db.DatabaseFactory;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet used for initializing Flyway and perform data migrations
 *
 */
public class FlywayServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(FlywayServlet.class);

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        LOG.info("Initializing Flyway...");
        Flyway flyway = new Flyway();
        flyway.setDataSource(DatabaseFactory.getConnectionManager().getDataSource());

        if (!flyway.isBaselineOnMigrate()) {
            LOG.info("Performing baseline...");
            flyway.baseline();
        }
        flyway.repair();
        int scripts = flyway.migrate();
        LOG.info("Flyway init completed. {} scripts were applied.", scripts);
    }
}
