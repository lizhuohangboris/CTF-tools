package org.springframework.cglib.core;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/core/ClassesKey.class */
public class ClassesKey {
    private static final Key FACTORY = (Key) KeyFactory.create(Key.class);

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/core/ClassesKey$Key.class */
    interface Key {
        Object newInstance(Object[] objArr);
    }

    private ClassesKey() {
    }

    public static Object create(Object[] array) {
        return FACTORY.newInstance(classNames(array));
    }

    private static String[] classNames(Object[] objects) {
        if (objects == null) {
            return null;
        }
        String[] classNames = new String[objects.length];
        for (int i = 0; i < objects.length; i++) {
            Object object = objects[i];
            if (object != null) {
                Class<?> aClass = object.getClass();
                classNames[i] = aClass == null ? null : aClass.getName();
            }
        }
        return classNames;
    }
}