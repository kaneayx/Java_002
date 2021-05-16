package rout;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 通过对注解RoutingWith进行AOP达到数据源切换
 */
@Aspect
@Component
public class RoutingAspect {

    @Around("@annotation(routingWith)")
    public Object routingWithDataSource(ProceedingJoinPoint joinPoint, RoutingWith routingWith) throws Throwable {
        String key = routingWith.value();
        System.out.println(key);
        try (RoutingDataSourceContext ctx = new RoutingDataSourceContext(key)) {
            return joinPoint.proceed();
        }
    }
}
