package io.homework08.kyle;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@Data
@NoArgsConstructor
@ToString
public class Student implements Serializable, BeanNameAware, ApplicationContextAware {

    private int id;
    private String name;

    private String beanName;
    private ApplicationContext applicationContext;

    public Student(int i, String name1) {
        id = i;
        name = name1;
    }

    public void init() {
        System.out.println("hello...........");
    }

    @Bean(name = "student100")
    public Student create() {
        return new Student(101, "KK101");
    }

    public void print() {
        System.out.println(this.beanName);
        System.out.println("   context.getBeanDefinitionNames() ===>> "
                + String.join(",", applicationContext.getBeanDefinitionNames()));
    }
}
