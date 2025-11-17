INSERT INTO organization_statistic (organization_id,
                                    feedback_total_count,
                                    feedback_confirmed_count,
                                    feedback_waiting_count,
                                    created_at,
                                    modified_at
)
SELECT o.id,
       COUNT(f.id),
       SUM(CASE WHEN f.status = 'CONFIRMED' THEN 1 ELSE 0 END),
       SUM(CASE WHEN f.status = 'WAITING' THEN 1 ELSE 0 END),
       NOW(6),
       NOW(6)
FROM organization o
         LEFT JOIN feedback f ON f.organization_id = o.id
GROUP BY o.id ON DUPLICATE KEY
UPDATE
    feedback_total_count = VALUES (feedback_total_count),
    feedback_confirmed_count = VALUES (feedback_confirmed_count),
    feedback_waiting_count = VALUES (feedback_waiting_count),
    modified_at = NOW(6);
