package rout;

import org.springframework.stereotype.Component;

@Component
public class School {
    // 读写操作
    @RoutingWith(RoutingDataSourceContext.DATA_SOURCE_MASTER)
    public void readAndWrite() {
        System.out.println("rout.School.readAndWrite()");
    }

    // 读操作
    @RoutingWith(RoutingDataSourceContext.DATA_SOURCE_SLAVE1)
    public void read() {
        System.out.println("rout.School.read()");
    }
}
