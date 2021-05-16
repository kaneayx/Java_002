package rout;

/**
 * 利用ThreadLocal存储数据源键值
 */
public class RoutingDataSourceContext implements AutoCloseable {
    static final ThreadLocal<String> threadLocalDataSourceKey = new ThreadLocal<>();

    public static final String DATA_SOURCE_MASTER = "masterDataSource";
    public static final String DATA_SOURCE_SLAVE1 = "slaveDataSource1";

    public static String getDataSourceRoutingKey() {
        String key = threadLocalDataSourceKey.get();
        return key == null ? DATA_SOURCE_MASTER : key;
    }

    public RoutingDataSourceContext(String key) {
        threadLocalDataSourceKey.set(key);
    }

    public void close() {
        threadLocalDataSourceKey.remove();
    }
}
