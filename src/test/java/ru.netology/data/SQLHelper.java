package ru.netology.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class SQLHelper {
    private static final QueryRunner runner = new QueryRunner();

    @SneakyThrows
    public static Connection getConn() {
        try {
            // Регистрируем драйвер базы данных
            Class.forName("com.mysql.cj.jdbc.Driver"); // Для MySQL
            // Class.forName("org.postgresql.Driver"); // Для PostgreSQL


            // Подключаемся к базе данных
            String url = "jdbc:mysql://localhost:3306/app"; // Для MySQL
            // String url = "jdbc:postgresql://localhost:5432/app"; // Для PostgreSQL


            String username = "app";
            String password = "pass";
            return DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Драйвер базы данных не найден", e);
        }
    }

    @SneakyThrows
    public static void setDown() {
        try (Connection conn = getConn()) {
            var sqlUpdateOne = "DELETE FROM credit_request_entity;";
            var sqlUpdateTwo = "DELETE FROM payment_entity;";
            var sqlUpdateThree = "DELETE FROM order_entity;";
            runner.update(conn, sqlUpdateOne);
            runner.update(conn, sqlUpdateTwo);
            runner.update(conn, sqlUpdateThree);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentEntity {
        private String id;
        private int amount;
        private Timestamp created;
        private String status;
        private String transaction_id;
    }

    @SneakyThrows
    public static List<PaymentEntity> getPayments() {
        try (Connection conn = getConn()) {
            var sqlQuery = "SELECT * FROM payment_entity ORDER BY created DESC;";
            ResultSetHandler<List<PaymentEntity>> resultHandler = new BeanListHandler<>(PaymentEntity.class);
            return runner.query(conn, sqlQuery, resultHandler);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreditRequestEntity {
        private String id;
        private String bank_id;
        private Timestamp created;
        private String status;
    }

    @SneakyThrows
    public static List<CreditRequestEntity> getCreditsRequest() {
        try (Connection conn = getConn()) {
            var sqlQuery = "SELECT * FROM credit_request_entity ORDER BY created DESC;";
            ResultSetHandler<List<CreditRequestEntity>> resultHandler = new BeanListHandler<>(CreditRequestEntity.class);
            return runner.query(conn, sqlQuery, resultHandler);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderEntity {
        private String id;
        private Timestamp created;
        private String credit_id;
        private String payment_id;
    }

    @SneakyThrows
    public static List<OrderEntity> getOrders() {
        try (Connection conn = getConn()) {
            var sqlQuery = "SELECT * FROM order_entity ORDER BY created DESC;";
            ResultSetHandler<List<OrderEntity>> resultHandler = new BeanListHandler<>(OrderEntity.class);
            return runner.query(conn, sqlQuery, resultHandler);
        }
    }
}