package org.springframework.beans;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/PropertyValues.class */
public interface PropertyValues extends Iterable<PropertyValue> {
    PropertyValue[] getPropertyValues();

    @Nullable
    PropertyValue getPropertyValue(String str);

    PropertyValues changesSince(PropertyValues propertyValues);

    boolean contains(String str);

    boolean isEmpty();

    @Override // java.lang.Iterable
    default Iterator<PropertyValue> iterator() {
        return Arrays.asList(getPropertyValues()).iterator();
    }

    @Override // java.lang.Iterable
    default Spliterator<PropertyValue> spliterator() {
        return Spliterators.spliterator(getPropertyValues(), 0);
    }

    default Stream<PropertyValue> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
}