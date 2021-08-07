package cn.c7n6y.springboot.personal_test.config;

import com.alibaba.fastjson.JSONObject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class LogAspect {
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Pointcut("execution(public * cn.c7n6y.springboot.personal_test.controller..*.*(..)) || execution(public * cn.c7n6y.springboot.personal_test.service..*.*(..))")
    public void logPointCut() {

    }

    @Before("logPointCut()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        saveRequestLog(joinPoint);
    }
    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        String transId = String.valueOf(System.currentTimeMillis());
        Object result = null;
        try {
            // 执行方法
            result = point.proceed();
            // 打印请求日志
//            saveRequestLog(point, transId);
        } catch (Exception e) {
            logger.error("test",e);
            // 打印异常日志
            saveExceptionLog(point, e.getMessage());
            throw e;
        }
        if (result != null) {
            saveResponseLog(point, result);
        }
        return result;
    }

    private void saveExceptionLog(ProceedingJoinPoint point, String exeMsg) {
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        String className = point.getTarget().getClass().getName();
        Object[] args = point.getArgs();
        logger.info(":类.方法" + className + "." + method.getName()  + ",捕获异常:" + exeMsg + ",请求参数:" + JSONObject.toJSONString(args));
    }

    private void saveRequestLog(JoinPoint point) {
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        String className = point.getTarget().getClass().getName();
        Object[] args = point.getArgs();
        logger.info(":类.方法" + className + "." + method.getName() + ",请求参数:" + JSONObject.toJSONString(args));
    }

    private void saveResponseLog(ProceedingJoinPoint point, Object respObj) {
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        String className = point.getTarget().getClass().getName();
        Object[] args = point.getArgs();
        logger.info(":类.方法" + className + "." + method.getName() + ",返回数据:" + respObj.toString());
    }
}
