package org.route;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class ExecutionLog {
    @Around("execution(* org.route..*Service.*(..))")
    public Object executeLog(ProceedingJoinPoint jp) throws Throwable {
        long started = System.nanoTime();
        try { return jp.proceed(); }
        finally {
            long elapsedMillis = (System.nanoTime() - started) / 1_000_000;
            System.out.println(jp.getSignature().toShortString() + " completed in " + elapsedMillis + " ms");
        }
    }
}
