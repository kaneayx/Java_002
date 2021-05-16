import com.mysql.cj.jdbc.JdbcStatement;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 插入 100 万订单模拟数据，测试不同方式的插入效率
 * 测试时已删除所有索引和约束
 *
 * 表结构
 * CREATE TABLE `t_order` (
 *   `f_id` bigint DEFAULT NULL COMMENT '订单Id',
 *   `f_no` varchar(45) DEFAULT NULL COMMENT '订单号',
 *   `f_user_id` int DEFAULT NULL COMMENT '用户Id',
 *   `f_product_id` int DEFAULT NULL COMMENT '商品id',
 *   `f_product_no` char(20) DEFAULT NULL COMMENT '商品编号',
 *   `f_product_name` varchar(45) DEFAULT NULL COMMENT '商品名称',
 *   `f_price` decimal(8,2) DEFAULT NULL COMMENT '价格',
 *   `f_created_at` bigint DEFAULT NULL COMMENT '创建时间',
 *   `f_updated_at` bigint DEFAULT NULL COMMENT '更新时间'
 * ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单表';
 */
public class Main {

    public static void main(String[] args) throws SQLException, IOException {

        try (Connection connection = getConnection()) {
            // insertByForeach(connection);
            // insertBatch(connection);
            insertByFile(connection);
        }
    }

    /**
     * 使用 load data local infile 命令进行文件插入
     * 构建耗时：5223 ms
     * 数据库耗时：13535 ms
     * 总耗时：18758 ms
     */
    private static void insertByFile(Connection connection) throws SQLException {
        long start1 = System.currentTimeMillis();
        StringBuilder builder = new StringBuilder();

        // "0,f_no,1011,26985,f_product_no,f_product_name,,,,,";
        for (int i = 0; i < 100_0000; i++) {
            builder.append(String.format("%d,%s,%d,%d,%s,%s,%f,%d,%d%n",
                    i, "f_no", 1011, 26985, "f_product_no", "f_product_name", BigDecimal.valueOf(59.63), start1, start1));
        }

        String sql = "load data local infile 'd:/data.txt' into table t_order"
                + " CHARACTER SET utf8" //  可选，避免中文乱码问题
                + " FIELDS TERMINATED BY ','" // 字段分隔符，每个字段(列)以什么字符分隔，默认是 \t
                + " OPTIONALLY ENCLOSED BY ''" // 文本限定符，每个字段被什么字符包围，默认是空字符
                + " ESCAPED BY '\\\\'" // 转义符，默认是 \
                + " LINES TERMINATED BY '\\n'" // 记录分隔符，如字段本身也含\n，那么应先去除，否则load data 会误将其视作另一行记录进行导入
                + " (`f_id`, `f_no`, `f_user_id`, `f_product_id`, `f_product_no`, `f_product_name`, `f_price`, `f_created_at`, `f_updated_at`)"; // 每一行文本按顺序对应的表字段，建议不要省略

        ByteArrayInputStream inputStream = new ByteArrayInputStream(builder.toString().getBytes(StandardCharsets.UTF_8));

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ((JdbcStatement) preparedStatement).setLocalInfileInputStream(inputStream);

            long start2 = System.currentTimeMillis();
            preparedStatement.execute();
            long end = System.currentTimeMillis();

            System.out.println("构建耗时：" + (start2 - start1) + " ms");
            System.out.println("数据库耗时：" + (end - start2) + " ms");
            System.out.println("总耗时：" + (end - start1) + " ms");
        }
    }

    /**
     * 批量插入(executeBatch)
     * 构建耗时：1293 ms
     * 数据库耗时：19783 ms
     * 总耗时：21076 ms
     */
    private static void insertBatch(Connection connection) throws SQLException {
        long start1 = System.currentTimeMillis();

        try (PreparedStatement preparedStatement = getStatement(connection, start1)) {
            for (int i = 0; i < 100_0000; i++) {
                preparedStatement.setLong(1, i);
                preparedStatement.addBatch();
            }

            long start2 = System.currentTimeMillis();
            preparedStatement.executeBatch();
            long end = System.currentTimeMillis();

            System.out.println("构建耗时：" + (start2 - start1) + " ms");
            System.out.println("数据库耗时：" + (end - start2) + " ms");
            System.out.println("总耗时：" + (end - start1) + " ms");
        }
    }

    /**
     * 直接循环插入
     * execute耗时：446609 ms
     * commit耗时：2179 ms
     * 总耗时：448788 ms
     */
    private static void insertByForeach(Connection connection) throws SQLException {
        long start1 = System.currentTimeMillis();
        try (PreparedStatement preparedStatement = getStatement(connection, start1)) {
            connection.setAutoCommit(false);
            for (int i = 0; i < 100_0000; i++) {
                preparedStatement.setLong(1, i);
                preparedStatement.execute();
            }

            long start2 = System.currentTimeMillis();
            connection.commit();
            long end = System.currentTimeMillis();

            System.out.println("execute耗时：" + (start2 - start1) + " ms");
            System.out.println("commit耗时：" + (end - start2) + " ms");
            System.out.println("总耗时：" + (end - start1) + " ms");
        }
    }

    private static PreparedStatement getStatement(Connection connection, long start1) throws SQLException {
        String sql = "INSERT INTO t_order (`f_id`, `f_no`, `f_user_id`, `f_product_id`, `f_product_no`, `f_product_name`, `f_price`, `f_created_at`, `f_updated_at`) VALUES (?,?,?,?,?,?,?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        String f_no = "f_no";
        long f_user_id = 1011;
        long f_product_id = 26985;
        String f_product_no = "f_product_no";
        String f_product_name = "f_product_name";
        BigDecimal f_price = BigDecimal.valueOf(59.63);

        // f_no
        preparedStatement.setString(2, f_no);
        // f_user_id
        preparedStatement.setLong(3, f_user_id);
        // f_product_id
        preparedStatement.setLong(4, f_product_id);
        // f_product_no
        preparedStatement.setString(5, f_product_no);
        // f_product_name
        preparedStatement.setString(6, f_product_name);
        // f_price
        preparedStatement.setBigDecimal(7, f_price);
        // f_created_at
        preparedStatement.setLong(8, start1);
        // f_updated_at
        preparedStatement.setLong(9, start1);

        return preparedStatement;
    }

    private static Connection getConnection() throws SQLException {
        // 使用rewriteBatchedStatements时语句最后不能加分号：';'
        String url = "jdbc:mysql://localhost:3306/new_schema?rewriteBatchedStatements=true&characterEncoding=UTF-8&allowLoadLocalInfile=true";
        return DriverManager.getConnection(url, "root", "root");
    }
}
