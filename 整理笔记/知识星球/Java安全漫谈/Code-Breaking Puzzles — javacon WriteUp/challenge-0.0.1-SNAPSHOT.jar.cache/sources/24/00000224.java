package com.fasterxml.classmate;

import com.fasterxml.classmate.util.ClassKey;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/classmate-1.4.0.jar:com/fasterxml/classmate/AnnotationOverrides.class */
public abstract class AnnotationOverrides implements Serializable {
    public abstract List<Class<?>> mixInsFor(ClassKey classKey);

    public List<Class<?>> mixInsFor(Class<?> beanClass) {
        return mixInsFor(new ClassKey(beanClass));
    }

    public static StdBuilder builder() {
        return new StdBuilder();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/classmate-1.4.0.jar:com/fasterxml/classmate/AnnotationOverrides$StdBuilder.class */
    public static class StdBuilder {
        protected final HashMap<ClassKey, List<Class<?>>> _targetsToOverrides = new HashMap<>();

        public StdBuilder add(Class<?> target, Class<?> mixin) {
            return add(new ClassKey(target), mixin);
        }

        public StdBuilder add(ClassKey target, Class<?> mixin) {
            List<Class<?>> mixins = this._targetsToOverrides.get(target);
            if (mixins == null) {
                mixins = new ArrayList<>();
                this._targetsToOverrides.put(target, mixins);
            }
            mixins.add(mixin);
            return this;
        }

        public AnnotationOverrides build() {
            return new StdImpl(this._targetsToOverrides);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/classmate-1.4.0.jar:com/fasterxml/classmate/AnnotationOverrides$StdImpl.class */
    public static class StdImpl extends AnnotationOverrides {
        protected final HashMap<ClassKey, List<Class<?>>> _targetsToOverrides;

        public StdImpl(HashMap<ClassKey, List<Class<?>>> overrides) {
            this._targetsToOverrides = new HashMap<>(overrides);
        }

        @Override // com.fasterxml.classmate.AnnotationOverrides
        public List<Class<?>> mixInsFor(ClassKey target) {
            return this._targetsToOverrides.get(target);
        }
    }
}