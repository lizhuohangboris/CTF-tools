package org.springframework.beans.factory.parsing;

import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/parsing/PassThroughSourceExtractor.class */
public class PassThroughSourceExtractor implements SourceExtractor {
    @Override // org.springframework.beans.factory.parsing.SourceExtractor
    public Object extractSource(Object sourceCandidate, @Nullable Resource definingResource) {
        return sourceCandidate;
    }
}