package com.rhys.testSourceCode.transaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/9/4 12:15 AM
 */
public class JDBCTransactionTestMain {
    private static final Log log = LogFactory.getLog(JDBCTransactionTestMain.class);

    public static void main(String[] args) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test?characterEncoding=utf-8&serverTimezone=UTC", "root", "980512@Nsd");
            statement = connection.createStatement();
            // 关闭自动提交
            connection.setAutoCommit(false);
            // 添加用户
            statement.executeUpdate("INSERT INTO tab_user (uid, name, age) VALUES('" + UUID.randomUUID().toString().replaceAll("-", "") + "', 'RhysNi', '26')");
            log.info("用户添加成功~");
            // 分配职业
            statement.executeUpdate("INSERT INTO tab_occupation (uName, occupation) VALUES('RhysNi', 'coder')");
            log.info("职业分配成功~");
            // 事务提交
            connection.commit();
            log.info("事务提交成功~");
        } catch (SQLException e) {
            try {
                log.info("数据操作异常开始回滚...");
                connection.rollback();
                log.info("数据回滚成功~");
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }

                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
