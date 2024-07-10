package org.springframework.aop.aspectj;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.AfterAdvice;
import org.springframework.aop.BeforeAdvice;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/aspectj/AspectJAopUtils.class */
public abstract class AspectJAopUtils {
    public static boolean isBeforeAdvice(Advisor anAdvisor) {
        AspectJPrecedenceInformation precedenceInfo = getAspectJPrecedenceInformationFor(anAdvisor);
        if (precedenceInfo != null) {
            return precedenceInfo.isBeforeAdvice();
        }
        return anAdvisor.getAdvice() instanceof BeforeAdvice;
    }

    public static boolean isAfterAdvice(Advisor anAdvisor) {
        AspectJPrecedenceInformation precedenceInfo = getAspectJPrecedenceInformationFor(anAdvisor);
        if (precedenceInfo != null) {
            return precedenceInfo.isAfterAdvice();
        }
        return anAdvisor.getAdvice() instanceof AfterAdvice;
    }

    @Nullable
    public static AspectJPrecedenceInformation getAspectJPrecedenceInformationFor(Advisor anAdvisor) {
        if (anAdvisor instanceof AspectJPrecedenceInformation) {
            return (AspectJPrecedenceInformation) anAdvisor;
        }
        Advice advice = anAdvisor.getAdvice();
        if (advice instanceof AspectJPrecedenceInformation) {
            return (AspectJPrecedenceInformation) advice;
        }
        return null;
    }
}