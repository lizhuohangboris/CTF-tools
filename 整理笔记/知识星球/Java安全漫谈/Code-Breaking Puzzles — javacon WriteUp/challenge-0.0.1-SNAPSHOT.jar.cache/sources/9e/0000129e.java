package org.springframework.aop.aspectj.autoproxy;

import java.util.Comparator;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJAopUtils;
import org.springframework.aop.aspectj.AspectJPrecedenceInformation;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/aspectj/autoproxy/AspectJPrecedenceComparator.class */
class AspectJPrecedenceComparator implements Comparator<Advisor> {
    private static final int HIGHER_PRECEDENCE = -1;
    private static final int SAME_PRECEDENCE = 0;
    private static final int LOWER_PRECEDENCE = 1;
    private final Comparator<? super Advisor> advisorComparator;

    public AspectJPrecedenceComparator() {
        this.advisorComparator = AnnotationAwareOrderComparator.INSTANCE;
    }

    public AspectJPrecedenceComparator(Comparator<? super Advisor> advisorComparator) {
        Assert.notNull(advisorComparator, "Advisor comparator must not be null");
        this.advisorComparator = advisorComparator;
    }

    @Override // java.util.Comparator
    public int compare(Advisor o1, Advisor o2) {
        int advisorPrecedence = this.advisorComparator.compare(o1, o2);
        if (advisorPrecedence == 0 && declaredInSameAspect(o1, o2)) {
            advisorPrecedence = comparePrecedenceWithinAspect(o1, o2);
        }
        return advisorPrecedence;
    }

    private int comparePrecedenceWithinAspect(Advisor advisor1, Advisor advisor2) {
        boolean oneOrOtherIsAfterAdvice = AspectJAopUtils.isAfterAdvice(advisor1) || AspectJAopUtils.isAfterAdvice(advisor2);
        int adviceDeclarationOrderDelta = getAspectDeclarationOrder(advisor1) - getAspectDeclarationOrder(advisor2);
        if (oneOrOtherIsAfterAdvice) {
            if (adviceDeclarationOrderDelta < 0) {
                return 1;
            }
            if (adviceDeclarationOrderDelta == 0) {
                return 0;
            }
            return -1;
        } else if (adviceDeclarationOrderDelta < 0) {
            return -1;
        } else {
            if (adviceDeclarationOrderDelta == 0) {
                return 0;
            }
            return 1;
        }
    }

    private boolean declaredInSameAspect(Advisor advisor1, Advisor advisor2) {
        return hasAspectName(advisor1) && hasAspectName(advisor2) && getAspectName(advisor1).equals(getAspectName(advisor2));
    }

    private boolean hasAspectName(Advisor anAdvisor) {
        return (anAdvisor instanceof AspectJPrecedenceInformation) || (anAdvisor.getAdvice() instanceof AspectJPrecedenceInformation);
    }

    private String getAspectName(Advisor anAdvisor) {
        AspectJPrecedenceInformation pi = AspectJAopUtils.getAspectJPrecedenceInformationFor(anAdvisor);
        Assert.state(pi != null, "Unresolvable precedence information");
        return pi.getAspectName();
    }

    private int getAspectDeclarationOrder(Advisor anAdvisor) {
        AspectJPrecedenceInformation precedenceInfo = AspectJAopUtils.getAspectJPrecedenceInformationFor(anAdvisor);
        if (precedenceInfo != null) {
            return precedenceInfo.getDeclarationOrder();
        }
        return 0;
    }
}