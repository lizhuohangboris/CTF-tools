package org.yaml.snakeyaml.external.com.google.gdata.util.common.base;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/external/com/google/gdata/util/common/base/Escaper.class */
public interface Escaper {
    String escape(String str);

    Appendable escape(Appendable appendable);
}