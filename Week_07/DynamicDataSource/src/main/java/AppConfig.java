import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import rout.RoutingDataSource;
import rout.RoutingDataSourceContext;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ComponentScan("rout")
@PropertySource("classpath:app.properties")
@EnableAspectJAutoProxy
public class AppConfig {
    // 主库
    @Bean(RoutingDataSourceContext.DATA_SOURCE_MASTER)
    DataSource masterDataSource(@Value("${jdbc.mysql.master}") String url,
                                @Value("${jdbc.mysql.master.username}") String username,
                                @Value("${jdbc.mysql.master.password}") String password) {
        return new DriverManagerDataSource(url, username, password);
    }

    // 从库1
    @Bean(RoutingDataSourceContext.DATA_SOURCE_SLAVE1)
    DataSource slaveDataSource(@Value("${jdbc.mysql.slave1}") String url,
                               @Value("${jdbc.mysql.slave1.username}") String username,
                               @Value("${jdbc.mysql.slave1.password}") String password) {
        return new DriverManagerDataSource(url, username, password);
    }

    // 生成动态数据源
    @Bean
    @Primary
    DataSource primaryDataSource(
            @Autowired @Qualifier(RoutingDataSourceContext.DATA_SOURCE_MASTER) DataSource masterDataSource,
            @Autowired @Qualifier(RoutingDataSourceContext.DATA_SOURCE_SLAVE1) DataSource slaveDataSource
    ) {
        Map<Object, Object> map = new HashMap<>();
        map.put(RoutingDataSourceContext.DATA_SOURCE_MASTER, masterDataSource);
        map.put(RoutingDataSourceContext.DATA_SOURCE_SLAVE1, slaveDataSource);
        RoutingDataSource routing = new RoutingDataSource();
        routing.setTargetDataSources(map);
        routing.setDefaultTargetDataSource(masterDataSource);
        return routing;
    }
}
