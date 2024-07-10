package org.yaml.snakeyaml.extensions.compactnotation;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/extensions/compactnotation/PackageCompactConstructor.class */
public class PackageCompactConstructor extends CompactConstructor {
    private String packageName;

    public PackageCompactConstructor(String packageName) {
        this.packageName = packageName;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.yaml.snakeyaml.constructor.Constructor
    public Class<?> getClassForName(String name) throws ClassNotFoundException {
        if (name.indexOf(46) < 0) {
            try {
                Class<?> clazz = Class.forName(this.packageName + "." + name);
                return clazz;
            } catch (ClassNotFoundException e) {
            }
        }
        return super.getClassForName(name);
    }
}