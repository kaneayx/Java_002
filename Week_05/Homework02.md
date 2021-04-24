# 第二题

写代码实现 Spring Bean 的装配，方式越多越好（XML、Annotation 都可以）, 提交到 GitHub。

### 1.使用XML配置

```xml
<bean id="student1" class="io.homework.kyle.Student">
    <property name="id" value="0110"/>
    <property name="name" value="Kyle"/>
</bean>
```

```java
@Data
public class Student {
    private int id;
    private String name;

    public void study() {
        System.out.println(name + " is studying.");
    }
}
```

```java
public static void main(String[] args) {
    ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
    Student student =(Student) context.getBean("student1");
    student.study();
}
```

### 2.使用@Autowired自动装配

```java
@Data
@Component
public class Student {
    private int id;
    private String name;

    public void study() {
        System.out.println( " is studying.");
    }
}
```

```java
public class XClass {
    @Autowired
    Student student1;

    public void show() {
        student1.study();
    }
}
```

```xml
<bean id="class1" class="io.homework.kyle.XClass"/>
<context:component-scan base-package="io.homework.kyle" />
```

```java
public static void main(String[] args) {
    ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
    XClass class1 = (XClass) context.getBean("class1");
    class1.show();
}
```

### 3.使用Java  Config

```java
@Data
public class Student {
    private int id;
    private String name;

    public void study() {
        System.out.println( " is studying.");
    }
}
```

```java
@Configuration
public class BeanConfig {
    @Bean
    public Student getStudent(){
        return  new Student();
    }
}
```

```java
public static void main(String[] args) {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BeanConfig.class);
    Student student = (Student) context.getBean(Student.class);
    student.study();
}
```



