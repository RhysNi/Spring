package com.rhys.testSourceCode.transaction.config;

import com.rhys.testSourceCode.transaction.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.transaction.*;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/9/7 9:09 PM
 */
@Data
@AllArgsConstructor
public class RhysDataSourceTransactionManager implements PlatformTransactionManager {
    private static final Log log = LogFactory.getLog(RhysDataSourceTransactionManager.class);

    private DataSource dataSource;

    @Override
    public TransactionStatus getTransaction(TransactionDefinition transactionDefinition) throws TransactionException {
        try {
            Connection connection = this.dataSource.getConnection();
            //关闭自动提交】
            connection.setAutoCommit(false);

            //将连接放入线程上下文中，通过TransactionSynchronizationManager将dataSource和connection绑定
            //保证后续同一事务中的业务操作获取到的都是相同的连接资源
            ConnectionHolder connectionHolder = new ConnectionHolder(connection);
            TransactionSynchronizationManager.bindResource(this.dataSource, connectionHolder);

            return new SimpleTransactionStatus(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void commit(TransactionStatus transactionStatus) throws TransactionException {
        // 回滚状态
        if (transactionStatus.isRollbackOnly()) {
            log.error("事务回滚~");
            this.rollback(transactionStatus);
        }
        Connection connection = ((ConnectionHolder) TransactionSynchronizationManager.getResource(this.dataSource)).getConnection();
        try {
            connection.commit();
            log.info("事务已提交~");
        } catch (SQLException e) {
            throw new TransactionUsageException("事务提交异常：", e);
        } finally {
            this.cleanup(connection);
        }
    }


    @Override
    public void rollback(TransactionStatus transactionStatus) throws TransactionException {
        Connection connection = ((ConnectionHolder) TransactionSynchronizationManager.getResource(this.dataSource)).getConnection();
        try {
            connection.rollback();
            log.error("事务回滚了~");
        } catch (SQLException e) {
            throw new TransactionUsageException("事务回滚异常：", e);
        } finally {
            this.cleanup(connection);
        }
    }

    private void cleanup(Connection connection) {
        // 解绑数据源
        TransactionSynchronizationManager.unbindResource(this.dataSource);
        // 开启自动提交
        try {
            connection.setAutoCommit(true);
            connection.close();
        } catch (SQLException e) {
            throw new TransactionUsageException("恢复自动提交失败：", e);
        }
    }
}
