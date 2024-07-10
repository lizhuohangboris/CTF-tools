package javax.validation.valueextraction;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/valueextraction/ValueExtractor.class */
public interface ValueExtractor<T> {

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/valueextraction/ValueExtractor$ValueReceiver.class */
    public interface ValueReceiver {
        void value(String str, Object obj);

        void iterableValue(String str, Object obj);

        void indexedValue(String str, int i, Object obj);

        void keyedValue(String str, Object obj, Object obj2);
    }

    void extractValues(T t, ValueReceiver valueReceiver);
}