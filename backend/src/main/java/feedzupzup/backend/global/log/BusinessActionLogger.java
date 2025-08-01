package feedzupzup.backend.global.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class BusinessActionLogger {

    private static final String LOG_FORMAT = "[SUCCESS] duration={}ms location={} method={} params={} response={}";
    private static final String NULL_RESULT = "null";
    private static final String TYPE_PREFIX = "(";
    private static final String TYPE_SUFFIX = ") ";

    @Around("@annotation(feedzupzup.backend.global.log.BusinessActionLog)")
    public Object logDomainAction(final ProceedingJoinPoint joinPoint) throws Throwable {
        final long startTime = System.currentTimeMillis();
        final Object result = joinPoint.proceed();
        final long duration = System.currentTimeMillis() - startTime;

        logSuccessfulExecution(joinPoint, result, duration);

        return result;
    }

    private void logSuccessfulExecution(final ProceedingJoinPoint joinPoint, final Object result, final long duration) {
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        final String methodName = signature.getName();
        final String location = getMethodLocation(signature);
        final Map<String, Object> params = getParametersWithNames(signature, joinPoint.getArgs());
        final String response = getResponseWithType(result);

        log.info(LOG_FORMAT, duration, location, methodName, params, response);
    }

    private Map<String, Object> getParametersWithNames(final MethodSignature signature, final Object[] args) {
        final Map<String, Object> paramMap = new LinkedHashMap<>();
        
        if (args.length == 0) {
            return paramMap;
        }
        
        final Parameter[] parameters = signature.getMethod().getParameters();
        for (int i = 0; i < args.length; i++) {
            final String paramName = parameters[i].getName();
            final Object paramValue = args[i];
            final String valueWithType = getValueWithType(paramValue);
            paramMap.put(paramName, valueWithType);
        }
        
        return paramMap;
    }
    
    private String getValueWithType(final Object value) {
        if (value == null) {
            return NULL_RESULT;
        }
        return TYPE_PREFIX + value.getClass().getSimpleName() + TYPE_SUFFIX + value;
    }
    
    private String getMethodLocation(final MethodSignature signature) {
        final String className = signature.getDeclaringType().getSimpleName();
        final String methodName = signature.getName();
        return className + "." + methodName + "()";
    }

    private String getResponseWithType(final Object result) {
        if (result == null) {
            return NULL_RESULT;
        }
        return TYPE_PREFIX + result.getClass().getSimpleName() + TYPE_SUFFIX + result;
    }
}
