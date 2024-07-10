package org.springframework.aop.aspectj;

import java.util.List;
import org.springframework.aop.Advisor;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/aspectj/AspectJProxyUtils.class */
public abstract class AspectJProxyUtils {
    public static boolean makeAdvisorChainAspectJCapableIfNecessary(List<Advisor> advisors) {
        if (!advisors.isEmpty()) {
            boolean foundAspectJAdvice = false;
            for (Advisor advisor : advisors) {
                if (isAspectJAdvice(advisor)) {
                    foundAspectJAdvice = true;
                }
            }
            if (foundAspectJAdvice && !advisors.contains(ExposeInvocationInterceptor.ADVISOR)) {
                advisors.add(0, ExposeInvocationInterceptor.ADVISOR);
                return true;
            }
            return false;
        }
        return false;
    }

    private static boolean isAspectJAdvice(Advisor advisor) {
        return (advisor instanceof InstantiationModelAwarePointcutAdvisor) || (advisor.getAdvice() instanceof AbstractAspectJAdvice) || ((advisor instanceof PointcutAdvisor) && (((PointcutAdvisor) advisor).getPointcut() instanceof AspectJExpressionPointcut));
    }
}