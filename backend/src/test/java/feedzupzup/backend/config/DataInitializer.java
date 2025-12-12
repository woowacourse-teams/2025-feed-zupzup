package feedzupzup.backend.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
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

    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteAll() {
        if (truncationDMLs.isEmpty()) {
            init();
        }

        // DB 초기화
        em.createNativeQuery(OFF_FOREIGN_CONSTRAINTS).executeUpdate();
        truncationDMLs.stream()
                .map(em::createNativeQuery)
                .forEach(Query::executeUpdate);
        em.createNativeQuery(ON_FOREIGN_CONSTRAINTS).executeUpdate();

        // Redis 초기화
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushDb();
    }

    private void init() {
        final List<String> tableNames = em.createNativeQuery("SHOW TABLES ").getResultList();

        tableNames.stream()
                .map(tableName -> String.format(TRUNCATE_SQL_FORMAT, tableName))
                .forEach(truncationDMLs::add);
    }
}
