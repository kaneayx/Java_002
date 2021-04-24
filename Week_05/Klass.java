package io.homework08.kyle;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class Klass {
    List<Student> students;

    public void dong() {
        System.out.println(this.getStudents());
    }
}
