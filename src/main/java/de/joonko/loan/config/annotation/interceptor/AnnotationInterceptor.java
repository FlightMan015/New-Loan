package de.joonko.loan.config.annotation.interceptor;

import de.joonko.loan.common.AppConstants;
import de.joonko.loan.common.utils.CommonUtils;
import de.joonko.loan.config.annotation.LoadAndClearLoggingContext;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AnnotationInterceptor {

    private final String MDC_TOKEN_KEY = AppConstants.SLF4J_MDC_LOGGING_NAME;


    @Pointcut("@annotation(de.joonko.loan.config.annotation.LoadAndClearLoggingContext)")
    public void annotatedMethod() {
    }

    @Pointcut("@within(de.joonko.loan.config.annotation.LoadAndClearLoggingContext)")
    public void annotatedClass() {
    }

    @Before("execution(* *(..)) && (annotatedMethod() || annotatedClass())")
    public void loadLoggingContext(JoinPoint thisJoinPoint) {
        LoadAndClearLoggingContext declaredAnnotation = (LoadAndClearLoggingContext) thisJoinPoint.getSignature().getDeclaringType().getAnnotation(LoadAndClearLoggingContext.class);
        String prefix = declaredAnnotation.prefix();
        MDC.put(MDC_TOKEN_KEY, CommonUtils.generateUUID(prefix));
    }

    @After("execution(* *(..)) && (annotatedMethod() || annotatedClass())")
    public void clearLoggingContext(JoinPoint thisJoinPoint) {
        MDC.remove(MDC_TOKEN_KEY);
    }

}
