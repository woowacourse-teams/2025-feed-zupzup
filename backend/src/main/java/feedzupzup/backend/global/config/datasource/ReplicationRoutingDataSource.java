package feedzupzup.backend.global.config.datasource;


import feedzupzup.backend.global.config.datasource.DataSourceConfig.DataSourceKey;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class ReplicationRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        boolean isTransactionReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();

        if (isTransactionReadOnly) {
            return DataSourceKey.READER;
        }
        return DataSourceKey.WRITER;
    }
}
