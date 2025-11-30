package feedzupzup.backend.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class DataInitializer {

    private static final String OFF_FOREIGN_CONSTRAINTS = "SET foreign_key_checks = false";
    private static final String ON_FOREIGN_CONSTRAINTS = "SET foreign_key_checks = true";
    private static final String TRUNCATE_SQL_FORMAT = "TRUNCATE %s";

    private static final List<String> truncationDMLs = new ArrayList<>();

    @PersistenceContext
    private EntityManager em;

    private final DataSource dataSource;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteAll() {
        if (truncationDMLs.isEmpty()) {
            init();
        }

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(OFF_FOREIGN_CONSTRAINTS);

            for (String truncateSql : truncationDMLs) {
                stmt.addBatch(truncateSql);
            }
            stmt.executeBatch();

            stmt.execute(ON_FOREIGN_CONSTRAINTS);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to truncate tables", e);
        }
    }

    private void init() {
        final List<String> tableNames = em.createNativeQuery("SHOW TABLES ").getResultList();

        tableNames.stream()
                .map(tableName -> String.format(TRUNCATE_SQL_FORMAT, tableName))
                .forEach(truncationDMLs::add);
    }
}
