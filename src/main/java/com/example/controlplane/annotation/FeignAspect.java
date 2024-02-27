package com.example.controlplane.annotation;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author 7bin
 * @date 2024/02/26
 */
@Slf4j
@Aspect
@Component
public class FeignAspect {

    /**
     * Mapper层切点 使用到了我们定义的 DynamicUrl 作为切点表达式。
     */
    @Pointcut("@annotation(com.example.controlplane.annotation.DynamicUrl)")
    public void dynamicUrlPointcut() {

    }

    @Around("dynamicUrlPointcut()")
    public Object Interceptor(ProceedingJoinPoint pjp) throws Throwable {
        Object result = null;

        result = pjp.proceed();

        return result;
    }

}
