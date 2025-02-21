package com.team1.beanstore.global.aspect;

import com.team1.beanstore.global.dto.RsData;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ResponseAspect {
    private final HttpServletResponse response;

    // 모든 ..beanstore 패키지 안에 있는 모든 컨트롤러에 대하여 RsData 데이터 처리를 해줍니다.
    @Around("execution(* com.team1.beanstore..*Controller.*(..))")
    public Object responseAspect(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();

        // 응답코드를 설정해준다
        if (result instanceof RsData<?> rsData) {
            int statusCode = rsData.getStatusCode();
            response.setStatus(statusCode);
        }

        return result;
    }
}
