package org.springframework.web.servlet.view.tiles3;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.tiles.TilesException;
import org.apache.tiles.preparer.PreparerException;
import org.apache.tiles.preparer.ViewPreparer;
import org.apache.tiles.preparer.factory.NoSuchPreparerException;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.WebApplicationContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/view/tiles3/SimpleSpringPreparerFactory.class */
public class SimpleSpringPreparerFactory extends AbstractSpringPreparerFactory {
    private final Map<String, ViewPreparer> sharedPreparers = new ConcurrentHashMap(16);

    @Override // org.springframework.web.servlet.view.tiles3.AbstractSpringPreparerFactory
    protected ViewPreparer getPreparer(String name, WebApplicationContext context) throws TilesException {
        ViewPreparer preparer = this.sharedPreparers.get(name);
        if (preparer == null) {
            synchronized (this.sharedPreparers) {
                preparer = this.sharedPreparers.get(name);
                if (preparer == null) {
                    try {
                        Class<?> beanClass = ClassUtils.forName(name, context.getClassLoader());
                        if (!ViewPreparer.class.isAssignableFrom(beanClass)) {
                            throw new PreparerException("Invalid preparer class [" + name + "]: does not implement ViewPreparer interface");
                        }
                        preparer = (ViewPreparer) context.getAutowireCapableBeanFactory().createBean(beanClass);
                        this.sharedPreparers.put(name, preparer);
                    } catch (ClassNotFoundException ex) {
                        throw new NoSuchPreparerException("Preparer class [" + name + "] not found", ex);
                    }
                }
            }
        }
        return preparer;
    }
}