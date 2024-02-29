package com.example.controlplane.annotation;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 计算节点远程调用切面
 *
 * @author 7bin
 * @date 2024/02/29
 */
@Slf4j
@Aspect
@Component
public class NodeRpcAspect {

    /**
     * Mapper层切点 使用到了我们定义的 NodeRpc 作为切点表达式。
     */
    @Pointcut("@annotation(com.example.controlplane.annotation.NodeRpc)")
    public void dynamicUrlPointcut() {

    }

    @Around("dynamicUrlPointcut()")
    public Object Interceptor(ProceedingJoinPoint pjp) throws Throwable {
        Object result = null;

        // 设置远程调用的 IP 和端口



        Method method = getMethod(pjp);
        // 获取方法的注解
        NodeRpc pageableCacheEnable = method.getAnnotation(NodeRpc.class);
        if (pageableCacheEnable == null) {
            return pjp.proceed();
        }



        result = pjp.proceed();

        return result;
    }

    /**
     * 获取被拦截方法对象
     */
    public Method getMethod(ProceedingJoinPoint pjp) {
        Signature signature = pjp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method targetMethod = methodSignature.getMethod();
        return targetMethod;
    }

}
