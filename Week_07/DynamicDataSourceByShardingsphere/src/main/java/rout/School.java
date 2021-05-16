package rout;

import org.springframework.stereotype.Component;

@Component
public class School {
    // 读写操作
    public void readAndWrite() {
        System.out.println("rout.School.readAndWrite()");
    }

    // 读操作
    public void read() {
        System.out.println("rout.School.read()");
    }
}
