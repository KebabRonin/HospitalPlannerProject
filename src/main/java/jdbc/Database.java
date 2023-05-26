package jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.metrics.MetricsTrackerFactory;
import com.zaxxer.hikari.metrics.prometheus.PrometheusMetricsTrackerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class Database {
    private static final String URL =
            "jdbc:postgresql://localhost:5432/HospitalPlanner";
    private static final String USER = "postgres";
    private static final String PASSWORD = "s2receptor";
    private static HikariDataSource dataSource = null;

    private Database() {
    }

    public static synchronized Connection getConnection() throws SQLException {
        if (dataSource == null) {
            createDataSource();
        }
        return dataSource.getConnection();
    }

    private static void createDataSource(){
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(URL);
        config.setUsername(USER);
        config.setPassword(PASSWORD);
        config.setMaximumPoolSize(50);
        dataSource = new HikariDataSource(config);
    }

    public static void closeDataSource() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
    }

    public static void rollback() {
        Connection connection = null;
        try {
            connection = getConnection();
            if (connection != null && !connection.isClosed()) {
                connection.rollback();
            }
        } catch (SQLException e) {
            System.err.println(e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e);
            }
        }
    }
}