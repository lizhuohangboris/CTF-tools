package org.yaml.snakeyaml.constructor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/constructor/CustomClassLoaderConstructor.class */
public class CustomClassLoaderConstructor extends Constructor {
    private ClassLoader loader;

    public CustomClassLoaderConstructor(ClassLoader cLoader) {
        this(Object.class, cLoader);
    }

    public CustomClassLoaderConstructor(Class<? extends Object> theRoot, ClassLoader theLoader) {
        super(theRoot);
        this.loader = CustomClassLoaderConstructor.class.getClassLoader();
        if (theLoader == null) {
            throw new NullPointerException("Loader must be provided.");
        }
        this.loader = theLoader;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.yaml.snakeyaml.constructor.Constructor
    public Class<?> getClassForName(String name) throws ClassNotFoundException {
        return Class.forName(name, true, this.loader);
    }
}