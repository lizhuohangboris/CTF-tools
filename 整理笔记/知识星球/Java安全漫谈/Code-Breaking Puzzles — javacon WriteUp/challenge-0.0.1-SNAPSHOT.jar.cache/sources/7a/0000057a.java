package com.fasterxml.jackson.datatype.jdk8;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.core.util.VersionUtil;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-datatype-jdk8-2.9.7.jar:com/fasterxml/jackson/datatype/jdk8/PackageVersion.class */
public final class PackageVersion implements Versioned {
    public static final Version VERSION = VersionUtil.parseVersion("2.9.7", "com.fasterxml.jackson.datatype", "jackson-datatype-jdk8");

    @Override // com.fasterxml.jackson.core.Versioned
    public Version version() {
        return VERSION;
    }
}