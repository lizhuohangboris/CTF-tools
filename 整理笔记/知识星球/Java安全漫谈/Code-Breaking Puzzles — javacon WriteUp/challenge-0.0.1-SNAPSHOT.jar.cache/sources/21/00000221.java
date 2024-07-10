package com.fasterxml.classmate;

import com.fasterxml.classmate.util.ClassKey;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.HashMap;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/classmate-1.4.0.jar:com/fasterxml/classmate/AnnotationConfiguration.class */
public abstract class AnnotationConfiguration implements Serializable {
    public abstract AnnotationInclusion getInclusionForClass(Class<? extends Annotation> cls);

    public abstract AnnotationInclusion getInclusionForConstructor(Class<? extends Annotation> cls);

    public abstract AnnotationInclusion getInclusionForField(Class<? extends Annotation> cls);

    public abstract AnnotationInclusion getInclusionForMethod(Class<? extends Annotation> cls);

    public abstract AnnotationInclusion getInclusionForParameter(Class<? extends Annotation> cls);

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/classmate-1.4.0.jar:com/fasterxml/classmate/AnnotationConfiguration$StdConfiguration.class */
    public static class StdConfiguration extends AnnotationConfiguration implements Serializable {
        protected final AnnotationInclusion _defaultInclusion;
        protected final HashMap<ClassKey, AnnotationInclusion> _inclusions = new HashMap<>();

        public StdConfiguration(AnnotationInclusion defaultBehavior) {
            this._defaultInclusion = defaultBehavior;
        }

        @Override // com.fasterxml.classmate.AnnotationConfiguration
        public AnnotationInclusion getInclusionForClass(Class<? extends Annotation> annotationType) {
            return _inclusionFor(annotationType);
        }

        @Override // com.fasterxml.classmate.AnnotationConfiguration
        public AnnotationInclusion getInclusionForConstructor(Class<? extends Annotation> annotationType) {
            return _inclusionFor(annotationType);
        }

        @Override // com.fasterxml.classmate.AnnotationConfiguration
        public AnnotationInclusion getInclusionForField(Class<? extends Annotation> annotationType) {
            return getInclusionForClass(annotationType);
        }

        @Override // com.fasterxml.classmate.AnnotationConfiguration
        public AnnotationInclusion getInclusionForMethod(Class<? extends Annotation> annotationType) {
            return getInclusionForClass(annotationType);
        }

        @Override // com.fasterxml.classmate.AnnotationConfiguration
        public AnnotationInclusion getInclusionForParameter(Class<? extends Annotation> annotationType) {
            return getInclusionForClass(annotationType);
        }

        public void setInclusion(Class<? extends Annotation> annotationType, AnnotationInclusion incl) {
            this._inclusions.put(new ClassKey(annotationType), incl);
        }

        protected AnnotationInclusion _inclusionFor(Class<? extends Annotation> annotationType) {
            ClassKey key = new ClassKey(annotationType);
            AnnotationInclusion beh = this._inclusions.get(key);
            return beh == null ? this._defaultInclusion : beh;
        }
    }
}