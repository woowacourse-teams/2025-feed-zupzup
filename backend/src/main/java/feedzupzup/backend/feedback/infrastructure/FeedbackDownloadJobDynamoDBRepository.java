package feedzupzup.backend.feedback.infrastructure;

import feedzupzup.backend.dynamodb.config.DynamoDBProperties;
import feedzupzup.backend.feedback.domain.vo.FeedbackDownloadJob;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
@RequiredArgsConstructor
public class FeedbackDownloadJobDynamoDBRepository {

    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private final DynamoDBProperties dynamoDBProperties;

    public FeedbackDownloadJob save(final FeedbackDownloadJob job) {
        final DynamoDbTable<FeedbackDownloadJob> table = getTable();
        table.putItem(job);
        return job;
    }

    public FeedbackDownloadJob getById(final String jobId) {
        final DynamoDbTable<FeedbackDownloadJob> table = getTable();
        final Key key = Key.builder()
                .partitionValue(jobId)
                .build();

        return table.getItem(key);
    }

    private DynamoDbTable<FeedbackDownloadJob> getTable() {
        return dynamoDbEnhancedClient.table(
                dynamoDBProperties.feedbackDownloadJobTableName(),
                TableSchema.fromBean(FeedbackDownloadJob.class)
        );
    }
}
