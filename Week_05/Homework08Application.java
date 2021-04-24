package io.homework08.kyle;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.Resource;

@SpringBootApplication

public class Homework08Application {

    public static void main(String[] args) {
        SpringApplication.run(Homework08Application.class, args);
    }
}
