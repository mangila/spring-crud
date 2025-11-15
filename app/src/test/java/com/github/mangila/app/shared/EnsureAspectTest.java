package com.github.mangila.app.shared;

import com.github.mangila.app.shared.exception.EnsureException;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                EnsureAspect.class,
        }
)
class EnsureAspectTest {

    @Autowired
    private EnsureAspect ensureAspect;

    @Test
    void beforeEnsureNoNullArgsEmployeeService() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{10, "test"});
        assertThatCode(() -> {
            ensureAspect.beforeEnsureNoNullArgsEmployeeService(joinPoint);
        }).doesNotThrowAnyException();
    }

    @Test
    void beforeEnsureNoNullArgsEmployeeServiceThrowsException() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{10, null});
        assertThatThrownBy(() -> {
            ensureAspect.beforeEnsureNoNullArgsEmployeeService(joinPoint);
        }).isInstanceOf(EnsureException.class);
    }
}