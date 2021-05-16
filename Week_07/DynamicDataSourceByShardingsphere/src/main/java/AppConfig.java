import org.apache.shardingsphere.driver.api.ShardingSphereDataSourceFactory;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmConfiguration;
import org.apache.shardingsphere.replicaquery.api.config.ReplicaQueryRuleConfiguration;
import org.apache.shardingsphere.replicaquery.api.config.rule.ReplicaQueryDataSourceRuleConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

@Configuration
@ComponentScan("rout")
@PropertySource("classpath:app.properties")
public class AppConfig {
    public static final String DATA_SOURCE_MASTER = "masterDataSource";
    public static final String DATA_SOURCE_SLAVE1 = "slaveDataSource1";

    // 主库
    @Bean(DATA_SOURCE_MASTER)
    DataSource masterDataSource(@Value("${jdbc.mysql.master}") String url,
                                @Value("${jdbc.mysql.master.username}") String username,
                                @Value("${jdbc.mysql.master.password}") String password) {
        return new DriverManagerDataSource(url, username, password);
    }

    // 从库1
    @Bean(DATA_SOURCE_SLAVE1)
    DataSource slaveDataSource(@Value("${jdbc.mysql.slave1}") String url,
                               @Value("${jdbc.mysql.slave1.username}") String username,
                               @Value("${jdbc.mysql.slave1.password}") String password) {
        return new DriverManagerDataSource(url, username, password);
    }

    // 使用ShardingSphere生成动态数据源
    @Bean
    @Primary
    DataSource primaryDataSource(
            @Autowired @Qualifier(DATA_SOURCE_MASTER) DataSource masterDataSource,
            @Autowired @Qualifier(DATA_SOURCE_SLAVE1) DataSource slaveDataSource
    ) throws SQLException {
        Map<String, DataSource> map = new HashMap<>();
        map.put(DATA_SOURCE_MASTER, masterDataSource);
        map.put(DATA_SOURCE_SLAVE1, slaveDataSource);

        //配置读写分离规则
        List<ReplicaQueryDataSourceRuleConfiguration> configurations = new ArrayList<>();
        configurations.add(new ReplicaQueryDataSourceRuleConfiguration("ds", DATA_SOURCE_MASTER, List.of(DATA_SOURCE_SLAVE1), "load_balancer"));
        Map<String, ShardingSphereAlgorithmConfiguration> loadBalancers = new HashMap<>();
        loadBalancers.put("load_balancer", new ShardingSphereAlgorithmConfiguration("ROUND_ROBIN", new Properties()));
        ReplicaQueryRuleConfiguration ruleConfiguration = new ReplicaQueryRuleConfiguration(configurations, loadBalancers);

        //创建DataSource
        return ShardingSphereDataSourceFactory.createDataSource(map, List.of(ruleConfiguration), new Properties());
    }
}
