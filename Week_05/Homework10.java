package io.homework.kyle;

import java.sql.*;

public class Homework10 {
    public static void main(String[] args) {
        try {
            JDBC();
            JDBCByBatch();
            Hikari();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void JDBCByBatch() throws SQLException, ClassNotFoundException {
        try (Connection connection = getConnection()) {
            operateByBatch(connection);
        }
    }

    /**
     * 连接池
     */
    private static void Hikari() throws SQLException, ClassNotFoundException {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl("jdbc:mysql://localhost:3306/new_schema");
        hikariDataSource.setUsername("root");
        hikariDataSource.setPassword("root");
        hikariDataSource.setConnectionTimeout(30000);
        hikariDataSource.setMaximumPoolSize(100);

        try (Connection connection = hikariDataSource.getConnection()) {
            operateByBatch(connection);
        }
    }

    private static void JDBC() throws SQLException, ClassNotFoundException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            String sql;

            // 增
            sql = "insert into student (name) values('kyle')";
            System.out.println("insert: " + statement.executeUpdate(sql));

            // 改
            sql = "update student set name='Kane' where name = 'kyle'";
            System.out.println("update: " + statement.executeUpdate(sql));

            // 删
            sql = "delete from student where name = 'kyle'";
            System.out.println("delete: " + statement.executeUpdate(sql));

            // 查
            sql = "select * from student";
            try (ResultSet resultSet = statement.executeQuery(sql)) {
                while (resultSet.next()) {
                    System.out.println("name: " + resultSet.getString("name"));
                }
            }
        }
    }

    /**
     * 事务加批量操作
     */
    private static void operateByBatch(Connection connection) throws SQLException {
        PreparedStatement preparedStatement = null;

        try {
            // 关闭自动提交
            connection.setAutoCommit(false);
            String sql;

            // 增
            sql = "insert into student (name) values(?)";
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < 5; i++) {
                preparedStatement.setString(1, "kyle" + i);
                preparedStatement.addBatch();
            }
            System.out.println("insert: " + preparedStatement.executeBatch().length);

            // 改
            sql = "update student set name='Kane' where name = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, "kyle");
            System.out.println("update: " + preparedStatement.executeUpdate());

            // 删
            sql = "delete from student where name = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, "kyle");
            System.out.println("delete: " + preparedStatement.executeUpdate());

            // 查
            sql = "select * from student";
            preparedStatement = connection.prepareStatement(sql);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    System.out.println("name: " + resultSet.getString("name"));
                }
            }

            // 提交事务
            connection.commit();
            // 恢复默认提交方式
            connection.setAutoCommit(true);

        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }

    private static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/new_schema";
        return DriverManager.getConnection(url, "root", "root");
    }
}
