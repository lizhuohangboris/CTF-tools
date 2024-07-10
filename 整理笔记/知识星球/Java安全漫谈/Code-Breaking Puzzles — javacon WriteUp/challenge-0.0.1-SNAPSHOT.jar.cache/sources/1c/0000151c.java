package org.springframework.boot.ansi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.springframework.core.env.PropertySource;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/ansi/AnsiPropertySource.class */
public class AnsiPropertySource extends PropertySource<AnsiElement> {
    private static final Iterable<MappedEnum<?>> MAPPED_ENUMS;
    private final boolean encode;

    static {
        List<MappedEnum<?>> enums = new ArrayList<>();
        enums.add(new MappedEnum<>("AnsiStyle.", AnsiStyle.class));
        enums.add(new MappedEnum<>("AnsiColor.", AnsiColor.class));
        enums.add(new MappedEnum<>("AnsiBackground.", AnsiBackground.class));
        enums.add(new MappedEnum<>("Ansi.", AnsiStyle.class));
        enums.add(new MappedEnum<>("Ansi.", AnsiColor.class));
        enums.add(new MappedEnum<>("Ansi.BG_", AnsiBackground.class));
        MAPPED_ENUMS = Collections.unmodifiableList(enums);
    }

    public AnsiPropertySource(String name, boolean encode) {
        super(name);
        this.encode = encode;
    }

    @Override // org.springframework.core.env.PropertySource
    public Object getProperty(String name) {
        if (StringUtils.hasLength(name)) {
            for (MappedEnum<?> mappedEnum : MAPPED_ENUMS) {
                if (name.startsWith(mappedEnum.getPrefix())) {
                    String enumName = name.substring(mappedEnum.getPrefix().length());
                    Iterator<?> it = mappedEnum.getEnums().iterator();
                    while (it.hasNext()) {
                        Enum<?> ansiEnum = (Enum) it.next();
                        if (ansiEnum.name().equals(enumName)) {
                            if (this.encode) {
                                return AnsiOutput.encode((AnsiElement) ansiEnum);
                            }
                            return ansiEnum;
                        }
                    }
                    continue;
                }
            }
            return null;
        }
        return null;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/ansi/AnsiPropertySource$MappedEnum.class */
    private static class MappedEnum<E extends Enum<E>> {
        private final String prefix;
        private final Set<E> enums;

        MappedEnum(String prefix, Class<E> enumType) {
            this.prefix = prefix;
            this.enums = EnumSet.allOf(enumType);
        }

        public String getPrefix() {
            return this.prefix;
        }

        public Set<E> getEnums() {
            return this.enums;
        }
    }
}