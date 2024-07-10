package org.springframework.boot.ansi;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/ansi/AnsiBackground.class */
public enum AnsiBackground implements AnsiElement {
    DEFAULT("49"),
    BLACK("40"),
    RED("41"),
    GREEN("42"),
    YELLOW("43"),
    BLUE("44"),
    MAGENTA("45"),
    CYAN("46"),
    WHITE("47"),
    BRIGHT_BLACK("100"),
    BRIGHT_RED("101"),
    BRIGHT_GREEN("102"),
    BRIGHT_YELLOW("103"),
    BRIGHT_BLUE("104"),
    BRIGHT_MAGENTA("105"),
    BRIGHT_CYAN("106"),
    BRIGHT_WHITE("107");
    
    private String code;

    AnsiBackground(String code) {
        this.code = code;
    }

    @Override // java.lang.Enum, org.springframework.boot.ansi.AnsiElement
    public String toString() {
        return this.code;
    }
}