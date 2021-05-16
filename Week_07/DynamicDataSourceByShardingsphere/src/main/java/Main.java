import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import rout.School;

public class Main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        //测试
        School school=context.getBean(School.class);
        school.readAndWrite();
        school.read();
    }
}
