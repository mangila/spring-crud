package com.github.mangila.api.shared;

import io.github.mangila.ensure4j.Ensure;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * Aspect-oriented programming (AOP)
 * Intercept method calls before or after the method execution.
 */
@Aspect
@Component
public class EnsureAspect {

    /**
     * Ensures that none of the method arguments in the EmployeeService are null before the method execution.
     * If any argument is null, an IllegalArgumentException will be thrown.
     *
     * @param joinPoint the join point representing the intercepted method call and its details,
     *                  including the arguments to be verified for null values.
     */
    @Before("within(com.github.mangila.api.service.EmployeeService)")
    public void beforeEnsureNoNullArgsEmployeeService(JoinPoint joinPoint) {
        var args = joinPoint.getArgs();
        for (Object arg : args) {
            Ensure.notNull(arg);
        }
    }
}
