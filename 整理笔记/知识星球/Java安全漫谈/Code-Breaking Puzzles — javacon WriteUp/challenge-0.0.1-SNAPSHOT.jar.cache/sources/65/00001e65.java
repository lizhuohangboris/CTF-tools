package org.springframework.core.env;

import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/env/SimpleCommandLinePropertySource.class */
public class SimpleCommandLinePropertySource extends CommandLinePropertySource<CommandLineArgs> {
    public SimpleCommandLinePropertySource(String... args) {
        super(new SimpleCommandLineArgsParser().parse(args));
    }

    public SimpleCommandLinePropertySource(String name, String[] args) {
        super(name, new SimpleCommandLineArgsParser().parse(args));
    }

    @Override // org.springframework.core.env.EnumerablePropertySource
    public String[] getPropertyNames() {
        return StringUtils.toStringArray(((CommandLineArgs) this.source).getOptionNames());
    }

    @Override // org.springframework.core.env.CommandLinePropertySource
    protected boolean containsOption(String name) {
        return ((CommandLineArgs) this.source).containsOption(name);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.core.env.CommandLinePropertySource
    @Nullable
    public List<String> getOptionValues(String name) {
        return ((CommandLineArgs) this.source).getOptionValues(name);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.core.env.CommandLinePropertySource
    public List<String> getNonOptionArgs() {
        return ((CommandLineArgs) this.source).getNonOptionArgs();
    }
}