package ru.itq.util.aspect;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class TaskExecutionAspect {

    @Around("execution(* ru.itq.util.tasks.executors.*.execute(..)) || " +
            "execution(* ru.itq.util.service.*.createDoc(..))")
    public Object logExecutionTaskTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        long startTime = System.currentTimeMillis();
        log.info("ASPECT -> Метод {} начал работу в: {}", methodName, startTime);
        try {
            return joinPoint.proceed();
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            log.info("ASPECT -> Метод {} завершил работу в: {}. Длительность: {} мс", methodName, endTime, duration);
        }
    }
}
